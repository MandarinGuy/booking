package org.mandarin.booking.utils;

import static org.mandarin.booking.utils.EnumFixture.randomEnum;
import static org.mandarin.booking.utils.MemberFixture.EmailGenerator.generateEmail;
import static org.mandarin.booking.utils.MemberFixture.NicknameGenerator.generateNickName;
import static org.mandarin.booking.utils.MemberFixture.PasswordGenerator.generatePassword;
import static org.mandarin.booking.utils.MemberFixture.UserIdGenerator.generateUserId;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import org.mandarin.booking.MemberAuthority;
import org.mandarin.booking.app.member.MemberCommandRepository;
import org.mandarin.booking.app.show.ShowCommandRepository;
import org.mandarin.booking.app.venue.HallCommandRepository;
import org.mandarin.booking.domain.member.Member;
import org.mandarin.booking.domain.member.Member.MemberCreateCommand;
import org.mandarin.booking.domain.member.SecurePasswordEncoder;
import org.mandarin.booking.domain.show.Show;
import org.mandarin.booking.domain.show.Show.Rating;
import org.mandarin.booking.domain.show.Show.ShowCreateCommand;
import org.mandarin.booking.domain.show.Show.Type;
import org.mandarin.booking.domain.show.ShowRegisterRequest;
import org.mandarin.booking.domain.venue.Hall;
import org.springframework.test.util.ReflectionTestUtils;

public record TestFixture(
        MemberCommandRepository memberRepository,
        ShowCommandRepository showRepository,
        HallCommandRepository hallRepository,
        SecurePasswordEncoder securePasswordEncoder
) {
    public Member insertDummyMember(String userId, String password) {
        var command = new MemberCreateCommand(
                generateNickName(),
                userId,
                password,
                generateEmail()
        );
        return memberRepository.insert(
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
        return memberRepository.insert(
                member
        );
    }

    public Member insertDummyMember() {
        return this.insertDummyMember(generateUserId(), generatePassword());
    }

    public Show insertDummyShow(LocalDate performanceStartDate, LocalDate performanceEndDate) {
        var command = ShowCreateCommand.from(
                new ShowRegisterRequest(
                        UUID.randomUUID().toString().substring(0, 5),
                        "MUSICAL",
                        "AGE12",
                        "synopsis",
                        "https://example.com/poster.jpg",
                        performanceStartDate,
                        performanceEndDate
                )
        );
        var show = Show.create(command);
        return showRepository.insert(show);
    }

    public Hall insertDummyHall() {
        var hall = Hall.create();
        return hallRepository.insert(hall);
    }

    public List<Show> generateShows(int showCount) {
        return IntStream.range(0, showCount)
                .mapToObj(i -> generateShow())
                .toList();
    }

    private Show generateShow() {
        var request = validShowRegisterRequest();
        var show = Show.create(ShowCreateCommand.from(request));
        return showRepository.insert(show);
    }

    private ShowRegisterRequest validShowRegisterRequest() {
        return new ShowRegisterRequest(
                UUID.randomUUID().toString().substring(0, 10),
                randomEnum(Type.class).name(),
                randomEnum(Rating.class).name(),
                "공연 줄거리",
                "https://example.com/poster.jpg",
                LocalDate.now(),
                LocalDate.now().plusDays(30)
        );
    }
}
