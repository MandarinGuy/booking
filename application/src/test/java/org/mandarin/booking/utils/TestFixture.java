package org.mandarin.booking.utils;

import static org.mandarin.booking.MemberAuthority.ADMIN;
import static org.mandarin.booking.domain.EntityInsertBuilder.forTable;
import static org.mandarin.booking.utils.EnumFixture.randomEnum;
import static org.mandarin.booking.utils.HallFixture.generateHallName;
import static org.mandarin.booking.utils.HallFixture.generateSectionRegisterRequest;
import static org.mandarin.booking.utils.MemberFixture.EmailGenerator.generateEmail;
import static org.mandarin.booking.utils.MemberFixture.NicknameGenerator.generateNickName;
import static org.mandarin.booking.utils.MemberFixture.PasswordGenerator.generatePassword;
import static org.mandarin.booking.utils.MemberFixture.UserIdGenerator.generateUserId;
import static org.mandarin.booking.utils.ShowFixture.generateShowScheduleCreateCommand;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;
import org.mandarin.booking.MemberAuthority;
import org.mandarin.booking.app.JdbcBatchUtils;
import org.mandarin.booking.domain.hall.Hall;
import org.mandarin.booking.domain.hall.Section;
import org.mandarin.booking.domain.hall.SectionRegisterRequest;
import org.mandarin.booking.domain.member.Member;
import org.mandarin.booking.domain.member.Member.MemberCreateCommand;
import org.mandarin.booking.domain.member.SecurePasswordEncoder;
import org.mandarin.booking.domain.show.Inventory;
import org.mandarin.booking.domain.show.Show;
import org.mandarin.booking.domain.show.Show.Rating;
import org.mandarin.booking.domain.show.Show.ShowCreateCommand;
import org.mandarin.booking.domain.show.Show.Type;
import org.mandarin.booking.domain.show.ShowDetailResponse.ShowScheduleResponse;
import org.mandarin.booking.domain.show.ShowRegisterRequest;
import org.mandarin.booking.domain.show.ShowRegisterRequest.GradeRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TestFixture {
    private final EntityManager entityManager;
    private final SecurePasswordEncoder securePasswordEncoder;
    private final JdbcBatchUtils jdbcBatchUtils;
    private volatile Member cachedDefaultMember;

    public TestFixture(EntityManager entityManager, SecurePasswordEncoder securePasswordEncoder,
                       JdbcBatchUtils jdbcBatchUtils) {
        this.entityManager = entityManager;
        this.securePasswordEncoder = securePasswordEncoder;
        this.jdbcBatchUtils = jdbcBatchUtils;
    }

    public Member getOrCreateDefaultMember() {
        Member local = cachedDefaultMember;
        if (local != null) {
            return local;
        }
        synchronized (this) {
            if (cachedDefaultMember == null) {
                cachedDefaultMember = insertDummyMember(generateUserId(), generatePassword());
            }
            return cachedDefaultMember;
        }
    }

    public Member insertDummyMember(String userId, String password) {
        var command = new MemberCreateCommand(
                generateNickName(),
                userId,
                password,
                generateEmail()
        );
        return memberInsert(
                Member.create(command, securePasswordEncoder)
        );
    }

    public Member insertDummyMember(String userId, String nickName, List<MemberAuthority> authorities) {
        var command = new MemberCreateCommand(
                nickName,
                userId,
                generatePassword(),
                generateEmail()
        );
        var member = Member.create(command, securePasswordEncoder);
        ReflectionTestUtils.setField(member, "authorities", authorities);
        return memberInsert(
                member
        );
    }

    public Member insertDummyMember(MemberAuthority memberAuthority) {
        return this.insertDummyMember(generateUserId(), generatePassword(), List.of(memberAuthority));
    }

    public Show insertDummyShow(LocalDate performanceStartDate, LocalDate performanceEndDate) {
        var member = insertDummyMember(generateUserId(), generatePassword(), List.of(ADMIN));
        var hall = insertDummyHall(member.getUserId());
        var command = ShowCreateCommand.from(
                new ShowRegisterRequest(
                        hall.getId(),
                        UUID.randomUUID().toString().substring(0, 5),
                        "MUSICAL",
                        "AGE12",
                        "synopsis",
                        "https://example.com/poster.jpg",
                        performanceStartDate,
                        performanceEndDate,
                        "KRW",
                        List.of(new ShowRegisterRequest.GradeRequest("VIP", 100000, 100))
                )
        );
        var show = Show.create(hall.getId(), command);
        return showInsert(show);
    }

    public Hall insertDummyHall(String userId) {
        List<SectionRegisterRequest> sections = generateSectionRegisterRequest(10, 100);
        return insertHallGraph(generateHallName(), userId, sections);
    }

    public void generateShows(int showCount, Type type) {
        var hall = insertDummyHall(generateUserId());
        var hallId = hall.getId();
        var today = LocalDate.now();
        var rows = IntStream.range(0, showCount)
                .mapToObj(i -> new ShowRow(
                        hallId,
                        UUID.randomUUID().toString().substring(0, 10),
                        type.name(),
                        randomEnum(Rating.class).name(),
                        "공연 줄거리",
                        "https://example.com/poster.jpg",
                        today,
                        today.plusDays(30),
                        "KRW"
                ))
                .toList();

        batchInsertShows(rows);
    }

    public Show generateShow(int scheduleCount) {
        var hall = insertDummyHall(generateUserId());
        var show = generateShow(hall.getId());

        for (int i = 0; i < scheduleCount; i++) {
            var command = generateShowScheduleCreateCommand(show);
            show.registerSchedule(command);
        }

        return showInsert(show);
    }

    public List<Show> generateShows(int showCount) {
        var hall = insertDummyHall(generateUserId());
        return IntStream.range(0, showCount)
                .mapToObj(i -> generateShow(hall.getId()))
                .toList();
    }

    public void generateShows(int showCount, Rating rating) {
        var hall = insertDummyHall(generateUserId());
        var hallId = hall.getId();
        var today = LocalDate.now();
        var rows = IntStream.range(0, showCount)
                .mapToObj(i -> new ShowRow(
                        hallId,
                        UUID.randomUUID().toString().substring(0, 10),
                        randomEnum(Type.class).name(),
                        rating.name(),
                        "공연 줄거리",
                        "https://example.com/poster.jpg",
                        today,
                        today.plusDays(30),
                        "KRW"
                ))
                .toList();

        batchInsertShows(rows);
    }

    public void generateShows(int showCount, String titlePart) {
        Random random = new Random();
        var hall = insertDummyHall(generateUserId());
        var hallId = hall.getId();
        var today = LocalDate.now();
        var rows = IntStream.range(0, showCount)
                .mapToObj(i -> new ShowRow(
                        hallId,
                        (char) random.nextInt('a', 'z') + titlePart + (char) random.nextInt('a', 'z'),
                        randomEnum(Type.class).name(),
                        randomEnum(Rating.class).name(),
                        "공연 줄거리",
                        "https://example.com/poster.jpg",
                        today,
                        today.plusDays(30),
                        "KRW"
                ))
                .toList();

        batchInsertShows(rows);
    }

    public void generateShows(int showCount, int before, int after) {
        Random random = new Random();
        var hall = insertDummyHall(generateUserId());
        var hallId = hall.getId();
        var rows = IntStream.range(0, showCount)
                .mapToObj(i -> new ShowRow(
                        hallId,
                        UUID.randomUUID().toString().substring(0, 10),
                        randomEnum(Type.class).name(),
                        randomEnum(Rating.class).name(),
                        "공연 줄거리",
                        "https://example.com/poster.jpg",
                        LocalDate.now().minusDays(random.nextInt(before)),
                        LocalDate.now().plusDays(random.nextInt(after)),
                        "KRW"
                ))
                .toList();

        batchInsertShows(rows);
    }

    public Show generateShow(List<GradeRequest> grades) {
        var hall = insertDummyHall(generateUserId());
        var hallId = hall.getId();
        var request = new ShowRegisterRequest(
                hallId,
                UUID.randomUUID().toString().substring(0, 10),
                randomEnum(Type.class).name(),
                randomEnum(Rating.class).name(),
                "공연 줄거리",
                "https://example.com/poster.jpg",
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                "KRW",
                grades
        );
        var show = Show.create(hallId, ShowCreateCommand.from(request));
        return showInsert(show);
    }

    public Show generateShowWithNoSynopsis(int scheduleCount) {
        var hall = insertDummyHall(generateUserId());
        var show = Show.create(hall.getId(), ShowCreateCommand.from(new ShowRegisterRequest(
                hall.getId(),
                UUID.randomUUID().toString().substring(0, 10),
                randomEnum(Type.class).name(),
                randomEnum(Rating.class).name(),
                null,
                "https://example.com/poster.jpg",
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                "KRW",
                List.of(new ShowRegisterRequest.GradeRequest("VIP", 100000, 100))
        )));

        for (int i = 0; i < scheduleCount; i++) {
            var command = generateShowScheduleCreateCommand(show);
            show.registerSchedule(command);
        }

        ReflectionTestUtils.setField(show, "synopsis", "");
        return showInsert(show);
    }

    public boolean existsHallName(String hallName) {
        return (entityManager.createQuery("SELECT COUNT(h) FROM Hall h WHERE h.hallName = :hallName")
                .setParameter("hallName", hallName)
                .getSingleResult() instanceof Long count) && count > 0;
    }

    public void removeShows() {
        entityManager.createQuery("DELETE FROM Grade ").executeUpdate();
        entityManager.createQuery("DELETE FROM Show ").executeUpdate();
    }

    public Member findMemberByUserId(String userId) {
        return entityManager.createQuery("SELECT m FROM Member m WHERE m.userId = :userId", Member.class)
                .setParameter("userId", userId)
                .getSingleResult();
    }

    public Hall findHallById(Long hallId) {
        return entityManager.createQuery("SELECT h FROM Hall h WHERE h.id = :hallId", Hall.class)
                .setParameter("hallId", hallId)
                .getSingleResult();
    }

    public Show findShowByTitle(String title) {
        return entityManager.createQuery(
                        "SELECT s FROM Show s " +
                        "JOIN FETCH s.grades grade " +
                        "WHERE s.title = :title", Show.class)
                .setParameter("title", title)
                .getSingleResult();
    }

    public boolean isMatchingScheduleInShow(ShowScheduleResponse res, Show show) {
        return !entityManager.createQuery(
                        "SELECT s FROM ShowSchedule s WHERE s.id = :scheduleId AND s.show.id = :showId", Object.class)
                .setParameter("scheduleId", res.scheduleId())
                .setParameter("showId", show.getId())
                .getResultList().isEmpty();
    }

    public List<Long> findSectionIdsByHallId(Long hallId) {
        return entityManager.createQuery(
                        "select s.id from Section s where s.hall.id = :hallId", Long.class)
                .setParameter("hallId", hallId)
                .getResultList();
    }

    public List<Long> findSeatIdsBySectionId(long sectionId) {
        return entityManager.createQuery(
                        "SELECT seat.id FROM Section section "
                        + "join section.seats as seat WHERE section.id = :sectionId",
                        Long.class)
                .setParameter("sectionId", sectionId)
                .getResultList();
    }

    public Map<Long, List<Long>> generateGradeSeatMapByShowIdAndSectionId(Long showId, Long sectionId) {
        var gradeIds = findGradeIdsByShowId(showId);
        var seatIds = entityManager.createQuery("SELECT seat.id FROM Seat seat WHERE seat.section.id = :sectionId",
                        Long.class)
                .setParameter("sectionId", sectionId)
                .getResultList();

        return gerateGradeSeatMap(gradeIds, seatIds);
    }

    public Inventory findInventoryByScheduleId(Long scheduleId) {
        return entityManager.createQuery(
                        "SELECT i FROM Inventory i JOIN FETCH i.states as state WHERE i.showScheduleId = :scheduleId",
                        Inventory.class)
                .setParameter("scheduleId", scheduleId)
                .getSingleResult();
    }

    private Hall insertHallGraph(String hallName, String userId, List<SectionRegisterRequest> sections) {
        var hall = new Hall(hallName, userId);
        entityManager.persist(hall);
        entityManager.flush();
        long hallId = hall.getId();

        var sectionInsert = forTable(
                "section",
                SectionRegisterRequest.class,
                Section.class)
                .withForeignKey(hallId)
                .bindAs(SectionRegisterRequest::sectionName, "name")
                .compile();
        jdbcBatchUtils.batchUpdate(sectionInsert.sql(), sections, (ps, s) -> sectionInsert.binder().bind(ps, s), 1000);

        var seatParams = sections.stream()
                .flatMap(sec -> sec.seats().stream()
                        .map(seat -> new Object[]{
                                sec.sectionName(),
                                seat.rowNumber(),
                                seat.seatNumber()
                        }))
                .toList();
        if (!seatParams.isEmpty()) {
            jdbcBatchUtils.batchUpdate(
                    "INSERT INTO seat (section_id, seat_row, seat_number) " +
                    "VALUES ((SELECT s.id FROM section s WHERE s.hall_id = ? AND s.name = ?), ?, ?)",
                    seatParams,
                    (ps, arr) -> {
                        ps.setLong(1, hallId);
                        ps.setString(2, (String) arr[0]);
                        ps.setString(3, (String) arr[1]);
                        ps.setString(4, (String) arr[2]);
                    },
                    1000
            );
        }

        return hall;
    }

    private Map<Long, List<Long>> gerateGradeSeatMap(List<Long> gradeIds, List<Long> seatIds) {
        Map<Long, List<Long>> result = new HashMap<>();
        var gradeCount = gradeIds.size();
        var seatCount = seatIds.size();
        int seatPerGrade = seatCount / gradeCount;
        int remainingSeats = seatCount % gradeCount;

        int currentIndex = 0;
        for (int i = 0; i < gradeCount; i++) {
            int seatsToAdd = seatPerGrade;
            if (i < remainingSeats) {
                seatsToAdd++;
            }

            result.put(gradeIds.get(i), seatIds.subList(currentIndex, currentIndex + seatsToAdd));
            currentIndex += seatsToAdd;
        }

        return result;
    }

    private Show generateShow(Long hallId) {
        var request = validShowRegisterRequest(hallId, randomEnum(Type.class).name(), randomEnum(Rating.class).name());
        var show = Show.create(hallId, ShowCreateCommand.from(request));
        return showInsert(show);
    }

    private ShowRegisterRequest validShowRegisterRequest(Long hallId, String type, String rating) {
        return new ShowRegisterRequest(
                hallId,
                UUID.randomUUID().toString().substring(0, 10),
                type,
                rating,
                "공연 줄거리",
                "https://example.com/poster.jpg",
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                "KRW",
                List.of(
                        new ShowRegisterRequest.GradeRequest("standing", 90000, 300),
                        new ShowRegisterRequest.GradeRequest("VIP", 100000, 100),
                        new ShowRegisterRequest.GradeRequest("R", 150000, 30),
                        new ShowRegisterRequest.GradeRequest("S", 180000, 10)
                )
        );
    }

    private Show showInsert(Show show) {
        entityManager.persist(show);
        return show;
    }

    private Member memberInsert(Member member) {
        entityManager.persist(member);
        return member;
    }

    private List<Long> findGradeIdsByShowId(Long showId) {
        return entityManager.createQuery("select g.id from Grade g where g.show.id = :showId ", Long.class)
                .setParameter("showId", showId)
                .getResultList();
    }

    private void batchInsertShows(List<ShowRow> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        var compiled = forTable("shows", ShowRow.class, Show.class)
                .autoBindAll()
                .compile();
        jdbcBatchUtils.batchUpdate(compiled.sql(), rows, (ps, row) -> compiled.binder().bind(ps, row), 1000);
    }

    public record ShowRow(Long hallId,
                          String title,
                          String type,
                          String rating,
                          String synopsis,
                          String posterUrl,
                          LocalDate performanceStartDate,
                          LocalDate performanceEndDate,
                          String currency) {
    }
}
