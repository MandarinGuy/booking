package org.mandarin.booking.utils;

import static org.mandarin.booking.MemberAuthority.ADMIN;
import static org.mandarin.booking.utils.EnumFixture.randomEnum;
import static org.mandarin.booking.utils.HallFixture.generateHallName;
import static org.mandarin.booking.utils.MemberFixture.EmailGenerator.generateEmail;
import static org.mandarin.booking.utils.MemberFixture.NicknameGenerator.generateNickName;
import static org.mandarin.booking.utils.MemberFixture.PasswordGenerator.generatePassword;
import static org.mandarin.booking.utils.MemberFixture.UserIdGenerator.generateUserId;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;
import org.mandarin.booking.MemberAuthority;
import org.mandarin.booking.domain.hall.Hall;
import org.mandarin.booking.domain.member.Member;
import org.mandarin.booking.domain.member.Member.MemberCreateCommand;
import org.mandarin.booking.domain.member.SecurePasswordEncoder;
import org.mandarin.booking.domain.show.Show;
import org.mandarin.booking.domain.show.Show.Rating;
import org.mandarin.booking.domain.show.Show.ShowCreateCommand;
import org.mandarin.booking.domain.show.Show.Type;
import org.mandarin.booking.domain.show.ShowDetailResponse.ShowScheduleResponse;
import org.mandarin.booking.domain.show.ShowRegisterRequest;
import org.mandarin.booking.domain.show.ShowScheduleCreateCommand;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TestFixture {
    private final EntityManager entityManager;
    private final SecurePasswordEncoder securePasswordEncoder;

    public TestFixture(EntityManager entityManager, SecurePasswordEncoder securePasswordEncoder) {
        this.entityManager = entityManager;
        this.securePasswordEncoder = securePasswordEncoder;
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

    public Member insertDummyMember() {
        return this.insertDummyMember(generateUserId(), generatePassword());
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
                        performanceEndDate
                )
        );
        var show = Show.create(hall.getId(), command);
        return showInsert(show);
    }

    public Hall insertDummyHall(String userId) {
        var hall = Hall.create(generateHallName(), userId);
        entityManager.persist(hall);
        return hall;
    }

    public Show generateShow(int scheduleCount) {
        var hall = insertDummyHall(generateUserId());
        var show = generateShow(hall.getId());

        for (int i = 0; i < scheduleCount; i++) {
            Random random = new Random();
            var startAt = LocalDateTime.now().plusDays(random.nextInt(0, 10));
            var command = new ShowScheduleCreateCommand(show.getId(),
                    startAt,
                    startAt.plusHours(random.nextInt(2, 5))
            );
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

    public void generateShows(int showCount, Type type) {
        var hall = insertDummyHall(generateUserId());
        IntStream.range(0, showCount)
                .forEach(i -> generateShow(hall.getId(), type));
    }

    public void generateShows(int showCount, Rating rating) {
        var hall = insertDummyHall(generateUserId());
        IntStream.range(0, showCount)
                .forEach(i -> generateShow(hall.getId(), rating));
    }

    public void generateShows(int showCount, String titlePart) {
        Random random = new Random();
        var hall = insertDummyHall(generateUserId());
        IntStream.range(0, showCount)
                .forEach(i -> {
                    var request = validShowRegisterRequest(hall.getId(),
                            randomEnum(Type.class).name(),
                            randomEnum(Rating.class).name());
                    var show = Show.create(hall.getId(), ShowCreateCommand.from(request));
                    ReflectionTestUtils.setField(show, "title",
                            (char) random.nextInt('a', 'z') + titlePart + (char) random.nextInt('a', 'z'));
                    showInsert(show);
                });
    }

    public void generateShows(int showCount, int before, int after) {
        Random random = new Random();
        var hall = insertDummyHall(generateUserId());
        var hallId = hall.getId();
        IntStream.range(0, showCount)
                .forEach(i -> {
                    var request = new ShowRegisterRequest(
                            hallId,
                            UUID.randomUUID().toString().substring(0, 10),
                            randomEnum(Type.class).name(),
                            randomEnum(Rating.class).name(),
                            "공연 줄거리",
                            "https://example.com/poster.jpg",
                            LocalDate.now().minusDays(random.nextInt(before)),
                            LocalDate.now().plusDays(random.nextInt(after))
                    );
                    var show = Show.create(hallId, ShowCreateCommand.from(request));
                    showInsert(show);
                });
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
                LocalDate.now().plusDays(30)
        )));

        for (int i = 0; i < scheduleCount; i++) {
            Random random = new Random();
            var startAt = LocalDateTime.now().plusDays(random.nextInt(0, 10));
            var command = new ShowScheduleCreateCommand(show.getId(),
                    startAt,
                    startAt.plusHours(random.nextInt(2, 5))
            );
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

    public boolean isMatchingScheduleInShow(ShowScheduleResponse res, Show show) {
        return !entityManager.createQuery(
                        "SELECT s FROM ShowSchedule s WHERE s.id = :scheduleId AND s.show.id = :showId", Object.class)
                .setParameter("scheduleId", res.getScheduleId())
                .setParameter("showId", show.getId())
                .getResultList().isEmpty();
    }

    private void generateShow(Long hallId, Type type) {
        var request = validShowRegisterRequest(hallId, type.name(), randomEnum(Rating.class).name());
        var show = Show.create(hallId, ShowCreateCommand.from(request));
        showInsert(show);
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
                LocalDate.now().plusDays(30)
        );
    }

    private void generateShow(Long hallId, Rating rating) {
        var request = validShowRegisterRequest(hallId, randomEnum(Type.class).name(), rating.name());
        var show = Show.create(hallId, ShowCreateCommand.from(request));
        showInsert(show);
    }

    public Show generateShow(Long hallId) {
        var request = validShowRegisterRequest(hallId, randomEnum(Type.class).name(), randomEnum(Rating.class).name());
        var show = Show.create(hallId, ShowCreateCommand.from(request));
        return showInsert(show);
    }

    private Show showInsert(Show show) {
        entityManager.persist(show);
        return show;
    }

    private Member memberInsert(Member member) {
        entityManager.persist(member);
        return member;
    }
}
