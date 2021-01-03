package de.mybureau.time.service.timer;

import de.mybureau.time.model.*;
import de.mybureau.time.repository.TimerEntryRepository;
import de.mybureau.time.service.client.ClientService;
import de.mybureau.time.service.client.NewClientRequest;
import de.mybureau.time.service.project.NewProjectRequest;
import de.mybureau.time.service.project.NewProjectTaskRequest;
import de.mybureau.time.service.project.ProjectService;
import de.mybureau.time.service.project.exception.ProjectTaskNotFoundException;
import de.mybureau.time.service.timer.exception.TimerInvalidStateException;
import de.mybureau.time.utils.DateTimeHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.when;

@Transactional
class TimerServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private TimerEntryRepository timerEntryRepository;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TimerService timerService;

    @MockBean
    private DateTimeHelper dateTimeHelper;

    private Client client;

    private Project project;

    private ProjectTask projectTaskA;

    private ProjectTask projectTaskB;


    @BeforeEach
    public void setup() {

        when(dateTimeHelper.nowInUtc()).thenReturn(LocalDateTime.now());

        client = clientService.addClient(NewClientRequest.builder()
                .name("Client A")
                .build());

        project = projectService.addProject(NewProjectRequest.builder()
                .clientId(client.getId())
                .name("Project A")
                .build());

        projectTaskA = projectService.addTask(NewProjectTaskRequest.builder()
                .projectId(project.getId())
                .name("Task A")
                .description("Test task")
                .build());

        projectTaskB = projectService.addTask(NewProjectTaskRequest.builder()
                .projectId(project.getId())
                .name("Task B")
                .description("Test task")
                .build());
    }

    @Test
    public void list_returnsOnlyNonDeletedEntriesForSpecifiedDate() throws Exception {
        // given
        newTimerEntry(LocalDateTime.of(2021, 1, 1, 23, 59), false);
        newTimerEntry(LocalDateTime.of(2021, 1, 1, 23, 59), true);

        newTimerEntry(LocalDateTime.of(2021, 1, 2, 0, 0), false);
        newTimerEntry(LocalDateTime.of(2021, 1, 2, 23, 59), false);
        newTimerEntry(LocalDateTime.of(2021, 1, 2, 12, 0), true);

        newTimerEntry(LocalDateTime.of(2021, 1, 3, 0, 0), false);
        newTimerEntry(LocalDateTime.of(2021, 1, 3, 0, 0), true);

        // when
        final var entriesForDate = timerService.list(ListRequest.builder()
                .date(LocalDate.of(2021, 1, 2))
                .build());

        // then
        assertThat(entriesForDate).hasSize(2);
        assertThat(entriesForDate).allSatisfy(te -> {
            assertThat(te.isDeleted()).isFalse();
            assertThat(te.getStarted()).isBetween(
                    LocalDateTime.of(LocalDate.of(2021, 1, 2), LocalTime.MIN),
                    LocalDateTime.of(LocalDate.of(2021, 1, 2), LocalTime.MAX));
        });
    }

    private TimerEntry newTimerEntry(LocalDateTime started,
                                     boolean deleted) {
        return newTimerEntry(started, 10, deleted);
    }

    private TimerEntry newTimerEntry(LocalDateTime started,
                                     Integer durationInMins,
                                     boolean deleted) {
        final var timerEntry = new TimerEntry();
        timerEntry.setInsertedTs(dateTimeHelper.nowInUtc());
        timerEntry.setTask(projectTaskA);
        timerEntry.setType(TimerEntryType.TIMER);
        timerEntry.setStarted(started);
        timerEntry.setNotes("Some random notes");
        timerEntry.setDeleted(deleted);

        if (durationInMins != null) {
            timerEntry.setDurationInSeconds(durationInMins * 60L);
        }

        if (deleted) {
            timerEntry.setDeletedTs(LocalDateTime.now());
        }

        return timerEntryRepository.save(timerEntry);
    }

    @Test
    public void list_returnsEmptyListWhenNoNonDeletedEntriesForSpecifiedDateFound() throws Exception {
        // given
        newTimerEntry(LocalDateTime.of(2021, 1, 1, 23, 59), false);
        newTimerEntry(LocalDateTime.of(2021, 1, 1, 23, 59), true);

        newTimerEntry(LocalDateTime.of(2021, 1, 2, 12, 0), true);

        newTimerEntry(LocalDateTime.of(2021, 1, 3, 0, 0), false);
        newTimerEntry(LocalDateTime.of(2021, 1, 3, 0, 0), true);

        // when
        final var entriesForDate = timerService.list(ListRequest.builder()
                .date(LocalDate.of(2021, 1, 2))
                .build());

        // then
        assertThat(entriesForDate).hasSize(0);
    }

    @Test
    public void startTimer_newTimerStartedForExistingTaskWithoutOffset() throws Exception {
        // given
        final var now = LocalDateTime.of(2021, 1, 1, 12, 30, 0);
        when(dateTimeHelper.nowInUtc()).thenReturn(now);

        // when
        final var newTimer = timerService.startTimer(StartTimerRequest.builder()
                .taskId(projectTaskA.getId())
                .notes("Some notes")
                .build());

        // then
        assertThat(newTimer.getId()).isGreaterThan(0);
        assertThat(newTimer.getType()).isEqualTo(TimerEntryType.TIMER);
        assertThat(newTimer.getDurationInSeconds()).isNull();
        assertThat(newTimer.getStarted()).isEqualTo(now);
        assertThat(newTimer.getTask().getId()).isEqualTo(projectTaskA.getId());
        assertThat(newTimer.getNotes()).isEqualTo("Some notes");
        assertThat(newTimer.getInsertedTs()).isEqualTo(now);
        assertThat(newTimer.isDeleted()).isFalse();
        assertThat(newTimer.getDeletedTs()).isNull();
        assertThat(newTimer.getUpdatedTs()).isNull();
    }

    @Test
    public void startTimer_newTimerFailsToStartWhenTaskNotExist() throws Exception {
        // given
        final var now = LocalDateTime.of(2021, 1, 1, 12, 30, 0);
        when(dateTimeHelper.nowInUtc()).thenReturn(now);

        // when + then
        assertThatCode(() -> timerService.startTimer(StartTimerRequest.builder()
                .taskId(100001)
                .notes("Some notes")
                .build())).isInstanceOf(ProjectTaskNotFoundException.class);
    }

    @Test
    public void startTimer_newTimerStartedForExistingTaskWithOffset() throws Exception {
        // given
        final var now = LocalDateTime.of(2021, 1, 1, 12, 30, 0);
        when(dateTimeHelper.nowInUtc()).thenReturn(now);

        // when
        final var newTimer = timerService.startTimer(StartTimerRequest.builder()
                .taskId(projectTaskA.getId())
                .notes("Some notes")
                .offsetInMinutes(23)
                .build());

        // then
        assertThat(newTimer.getId()).isGreaterThan(0);
        assertThat(newTimer.getType()).isEqualTo(TimerEntryType.TIMER);
        assertThat(newTimer.getDurationInSeconds()).isNull();
        assertThat(newTimer.getStarted()).isEqualTo(LocalDateTime.of(2021, 1, 1, 12, 7, 0));
        assertThat(newTimer.getTask().getId()).isEqualTo(projectTaskA.getId());
        assertThat(newTimer.getNotes()).isEqualTo("Some notes");
        assertThat(newTimer.getInsertedTs()).isEqualTo(now);
        assertThat(newTimer.isDeleted()).isFalse();
        assertThat(newTimer.getDeletedTs()).isNull();
        assertThat(newTimer.getUpdatedTs()).isNull();
    }

    @Test
    public void manualEntry_newTimerCreatedWithProvidedDurationAndStartedTs() throws Exception {
        // given
        final var now = LocalDateTime.of(2021, 1, 1, 12, 30, 0);
        final var started = LocalDateTime.of(2021, 1, 1, 12, 11, 0);
        when(dateTimeHelper.nowInUtc()).thenReturn(now);

        // when
        final var newTimer = timerService.manualEntry(ManualEntryRequest.builder()
                .taskId(projectTaskA.getId())
                .durationInMinutes(15)
                .startedInUtc(started)
                .notes("Some notes")
                .build());

        // then
        assertThat(newTimer.getId()).isGreaterThan(0);
        assertThat(newTimer.getType()).isEqualTo(TimerEntryType.MANUAL);
        assertThat(newTimer.getDurationInSeconds()).isEqualTo(900);
        assertThat(newTimer.getStarted()).isEqualTo(started);
        assertThat(newTimer.getTask().getId()).isEqualTo(projectTaskA.getId());
        assertThat(newTimer.getNotes()).isEqualTo("Some notes");
        assertThat(newTimer.getInsertedTs()).isEqualTo(now);
        assertThat(newTimer.isDeleted()).isFalse();
        assertThat(newTimer.getDeletedTs()).isNull();
        assertThat(newTimer.getUpdatedTs()).isNull();
    }

    @Test
    public void manualEntry_newTimerCreatedWithDurationAndStartedTsAsNow_whenStartedTsNotProvided() throws Exception {
        // given
        final var now = LocalDateTime.of(2021, 1, 1, 12, 30, 0);
        when(dateTimeHelper.nowInUtc()).thenReturn(now);

        // when
        final var newTimer = timerService.manualEntry(ManualEntryRequest.builder()
                .taskId(projectTaskA.getId())
                .durationInMinutes(15)
                .notes("Some notes")
                .build());

        // then
        assertThat(newTimer.getId()).isGreaterThan(0);
        assertThat(newTimer.getType()).isEqualTo(TimerEntryType.MANUAL);
        assertThat(newTimer.getDurationInSeconds()).isEqualTo(900);
        assertThat(newTimer.getStarted()).isEqualTo(now);
        assertThat(newTimer.getTask().getId()).isEqualTo(projectTaskA.getId());
        assertThat(newTimer.getNotes()).isEqualTo("Some notes");
        assertThat(newTimer.getInsertedTs()).isEqualTo(now);
        assertThat(newTimer.isDeleted()).isFalse();
        assertThat(newTimer.getDeletedTs()).isNull();
        assertThat(newTimer.getUpdatedTs()).isNull();
    }

    @Test
    public void stopTimer_timerStoppedIfRunning() throws Exception {
        // given
        final var now = LocalDateTime.of(2021, 1, 1, 12, 30, 0);
        when(dateTimeHelper.nowInUtc()).thenReturn(now);

        // when
        final var newTimer = timerService.startTimer(StartTimerRequest.builder()
                .taskId(projectTaskA.getId())
                .notes("Some notes")
                .build());

        final var nowOnStop = LocalDateTime.of(2021, 1, 1, 12, 30, 34);
        when(dateTimeHelper.nowInUtc()).thenReturn(nowOnStop);

        timerService.stopTimer(StopTimerRequest.builder()
                .timerId(newTimer.getId())
                .build());

        // then
        assertThat(timerService.findTimer(newTimer.getId())).satisfies(timer -> {
            assertThat(timer.getId()).isEqualTo(newTimer.getId());
            assertThat(timer.getType()).isEqualTo(TimerEntryType.TIMER);
            assertThat(timer.getDurationInSeconds()).isEqualTo(34);
            assertThat(timer.getStarted()).isEqualTo(LocalDateTime.of(2021, 1, 1, 12, 30, 0));
            assertThat(timer.getTask().getId()).isEqualTo(projectTaskA.getId());
            assertThat(timer.getNotes()).isEqualTo("Some notes");
            assertThat(timer.getInsertedTs()).isEqualTo(now);
            assertThat(timer.isDeleted()).isFalse();
            assertThat(timer.getDeletedTs()).isNull();
            assertThat(timer.getUpdatedTs()).isEqualTo(nowOnStop);
        });
    }

    @Test
    public void stopTimer_alreadyStoppedTimerCannotBeStoppedAgain() throws Exception {
        // given
        final var now = LocalDateTime.of(2021, 1, 1, 12, 30, 0);
        when(dateTimeHelper.nowInUtc()).thenReturn(now);

        // when
        final var newTimer = timerService.startTimer(StartTimerRequest.builder()
                .taskId(projectTaskA.getId())
                .notes("Some notes")
                .build());

        final var nowOnStop = LocalDateTime.of(2021, 1, 1, 12, 30, 34);
        when(dateTimeHelper.nowInUtc()).thenReturn(nowOnStop);

        timerService.stopTimer(StopTimerRequest.builder()
                .timerId(newTimer.getId())
                .build());

        // then
        assertThatCode(() -> timerService.stopTimer(StopTimerRequest.builder()
                .timerId(newTimer.getId())
                .build())).isInstanceOf(TimerInvalidStateException.class);
    }

    @Test
    public void stopRunningTimer_whenThereIsOneTimerRunningToday_thenItsStopped() throws Exception {
        // given
        newTimerEntry(LocalDateTime.of(2021, 1, 1, 23, 59), null, false);
        newTimerEntry(LocalDateTime.of(2021, 1, 1, 23, 59), null, true);

        final var toBeStopped = newTimerEntry(LocalDateTime.of(2021, 1, 2, 0, 0), null, false);
        newTimerEntry(LocalDateTime.of(2021, 1, 2, 12, 0), null, true);
        newTimerEntry(LocalDateTime.of(2021, 1, 2, 23, 59), 15, false);

        when(dateTimeHelper.todayInUtc()).thenReturn(LocalDate.of(2021, 1, 2));
        when(dateTimeHelper.nowInUtc()).thenReturn(LocalDateTime.of(2021, 1, 2, 0, 37));

        // when
        timerService.stopRunningTimer();

        // then
        assertThat(timerService.findTimer(toBeStopped.getId())).satisfies(timer -> {
            assertThat(timer.getDurationInSeconds()).isEqualTo(37 * 60);
            assertThat(timer.getUpdatedTs()).isEqualTo(LocalDateTime.of(2021, 1, 2, 0, 37));
        });
    }

    @Test
    public void stopRunningTimer_runningTimerCannotBeStopped_ifThereAreMoreThanOneToday() throws Exception {
        // given
        newTimerEntry(LocalDateTime.of(2021, 1, 1, 23, 59), null, false);
        newTimerEntry(LocalDateTime.of(2021, 1, 1, 23, 59), null, true);

        newTimerEntry(LocalDateTime.of(2021, 1, 2, 0, 0), null, false);
        newTimerEntry(LocalDateTime.of(2021, 1, 2, 12, 0), null, true);
        newTimerEntry(LocalDateTime.of(2021, 1, 2, 23, 59), null, false);

        when(dateTimeHelper.todayInUtc()).thenReturn(LocalDate.of(2021, 1, 2));
        when(dateTimeHelper.nowInUtc()).thenReturn(LocalDateTime.of(2021, 1, 2, 0, 37));

        // when + then
        assertThatCode(() -> timerService.stopRunningTimer())
                .isInstanceOf(TimerInvalidStateException.class);
    }

    @Test
    public void stopRunningTimer_runningTimerCannotBeStopped_ifThereAreNoRunningTimers() throws Exception {
        // given
        newTimerEntry(LocalDateTime.of(2021, 1, 1, 23, 59), null, false);
        newTimerEntry(LocalDateTime.of(2021, 1, 1, 23, 59), null, true);

        newTimerEntry(LocalDateTime.of(2021, 1, 2, 0, 0), 12, false);
        newTimerEntry(LocalDateTime.of(2021, 1, 2, 12, 0), null, true);
        newTimerEntry(LocalDateTime.of(2021, 1, 2, 23, 59), 14, false);

        when(dateTimeHelper.todayInUtc()).thenReturn(LocalDate.of(2021, 1, 2));
        when(dateTimeHelper.nowInUtc()).thenReturn(LocalDateTime.of(2021, 1, 2, 0, 37));

        // when + then
        assertThatCode(() -> timerService.stopRunningTimer())
                .isInstanceOf(TimerInvalidStateException.class);
    }

    @Test
    public void resumeLastStoppedTimer_lastStoppedTimerFromTodayResumed_ifThereIsSuchAndNoCurrentlyRunningTimers() throws Exception {
        // given

        when(dateTimeHelper.nowInUtc()).thenReturn(LocalDateTime.of(2021, 1, 1, 22, 37));

        newTimerEntry(LocalDateTime.of(2021, 1, 1, 23, 59), 20, false);
        newTimerEntry(LocalDateTime.of(2021, 1, 1, 23, 59), 20, true);

        when(dateTimeHelper.nowInUtc()).thenReturn(LocalDateTime.of(2021, 1, 2, 21, 37));

        newTimerEntry(LocalDateTime.of(2021, 1, 2, 0, 0), 25, false);
        newTimerEntry(LocalDateTime.of(2021, 1, 2, 12, 0), null, true);
        final var toBeResumed = newTimerEntry(LocalDateTime.of(2021, 1, 2, 21, 59), 15, false);

        when(dateTimeHelper.todayInUtc()).thenReturn(LocalDate.of(2021, 1, 2));
        when(dateTimeHelper.nowInUtc()).thenReturn(LocalDateTime.of(2021, 1, 2, 22, 37));

        // when
        timerService.resumeLastStoppedTimer();

        // then
        final var lastTimer = timerService.list(ListRequest.forDate(LocalDate.of(2021, 1, 2)))
                .stream()
                .max(Comparator.comparing(TimerEntry::getInsertedTs));

        assertThat(lastTimer).hasValueSatisfying(lt -> {
            assertThat(lt.getId()).isNotEqualTo(toBeResumed.getId());
            assertThat(lt.getType()).isEqualTo(TimerEntryType.TIMER);
            assertThat(lt.getDurationInSeconds()).isNull();
            assertThat(lt.getStarted()).isEqualTo(LocalDateTime.of(2021, 1, 2, 22, 37));
            assertThat(lt.getTask().getId()).isEqualTo(projectTaskA.getId());
            assertThat(lt.getNotes()).isEqualTo(toBeResumed.getNotes());
            assertThat(lt.getInsertedTs()).isEqualTo(LocalDateTime.of(2021, 1, 2, 22, 37));
            assertThat(lt.isDeleted()).isFalse();
            assertThat(lt.getDeletedTs()).isNull();
            assertThat(lt.getUpdatedTs()).isNull();
        });
    }

    @Test
    public void resumeLastStoppedTimer_cannotBeResumed_whenThereAreNoStoppedTimers() throws Exception {
        // given
        when(dateTimeHelper.nowInUtc()).thenReturn(LocalDateTime.of(2021, 1, 1, 22, 37));

        newTimerEntry(LocalDateTime.of(2021, 1, 1, 23, 59), 20, false);
        newTimerEntry(LocalDateTime.of(2021, 1, 1, 23, 59), 20, true);

        when(dateTimeHelper.nowInUtc()).thenReturn(LocalDateTime.of(2021, 1, 2, 21, 37));

        newTimerEntry(LocalDateTime.of(2021, 1, 2, 12, 0), null, true);

        when(dateTimeHelper.todayInUtc()).thenReturn(LocalDate.of(2021, 1, 2));
        when(dateTimeHelper.nowInUtc()).thenReturn(LocalDateTime.of(2021, 1, 2, 22, 37));

        // when
        assertThatCode(() -> timerService.resumeLastStoppedTimer())
                .isInstanceOf(TimerInvalidStateException.class);
    }

    @Test
    public void resumeLastStoppedTimer_cannotBeResumed_whenThereIsRunningTimer() throws Exception {
        // given
        when(dateTimeHelper.nowInUtc()).thenReturn(LocalDateTime.of(2021, 1, 1, 22, 37));

        newTimerEntry(LocalDateTime.of(2021, 1, 1, 23, 59), 20, false);
        newTimerEntry(LocalDateTime.of(2021, 1, 1, 23, 59), 20, true);

        when(dateTimeHelper.nowInUtc()).thenReturn(LocalDateTime.of(2021, 1, 2, 21, 37));

        newTimerEntry(LocalDateTime.of(2021, 1, 2, 0, 0), 25, false);
        newTimerEntry(LocalDateTime.of(2021, 1, 2, 12, 0), null, false);
        newTimerEntry(LocalDateTime.of(2021, 1, 2, 21, 59), 15, false);

        when(dateTimeHelper.todayInUtc()).thenReturn(LocalDate.of(2021, 1, 2));
        when(dateTimeHelper.nowInUtc()).thenReturn(LocalDateTime.of(2021, 1, 2, 22, 37));

        // when
        assertThatCode(() -> timerService.resumeLastStoppedTimer())
                .isInstanceOf(TimerInvalidStateException.class);
    }

    @Test
    public void resumeTimer_stoppedTimerCanBeResumed() throws Exception {
        // given
        final var toBeResumed = newTimerEntry(LocalDateTime.of(2021, 1, 2, 12, 59), 20, false);

        // when
        when(dateTimeHelper.todayInUtc()).thenReturn(LocalDate.of(2021, 1, 2));
        when(dateTimeHelper.nowInUtc()).thenReturn(LocalDateTime.of(2021, 1, 2, 22, 37));

        final var resumedTimer = timerService.resumeTimer(ResumeTimerRequest.forTimerId(toBeResumed.getId()));

        // then
        assertThat(resumedTimer).satisfies(resumed -> {
            assertThat(resumed.getId()).isNotEqualTo(toBeResumed.getId());
            assertThat(resumed.getType()).isEqualTo(TimerEntryType.TIMER);
            assertThat(resumed.getDurationInSeconds()).isNull();
            assertThat(resumed.getStarted()).isEqualTo(LocalDateTime.of(2021, 1, 2, 22, 37));
            assertThat(resumed.getTask().getId()).isEqualTo(projectTaskA.getId());
            assertThat(resumed.getNotes()).isEqualTo(toBeResumed.getNotes());
            assertThat(resumed.getInsertedTs()).isEqualTo(LocalDateTime.of(2021, 1, 2, 22, 37));
            assertThat(resumed.isDeleted()).isFalse();
            assertThat(resumed.getDeletedTs()).isNull();
            assertThat(resumed.getUpdatedTs()).isNull();
        });
    }

    @Test
    public void resumeTimer_runningTimerCanNotBeResumed() throws Exception {
        // given
        final var toBeResumed = newTimerEntry(LocalDateTime.of(2021, 1, 2, 12, 59), null, false);

        // when
        when(dateTimeHelper.todayInUtc()).thenReturn(LocalDate.of(2021, 1, 2));
        when(dateTimeHelper.nowInUtc()).thenReturn(LocalDateTime.of(2021, 1, 2, 22, 37));

        assertThatCode(() -> timerService.resumeTimer(ResumeTimerRequest.forTimerId(toBeResumed.getId())))
                .isInstanceOf(TimerInvalidStateException.class);
    }
}