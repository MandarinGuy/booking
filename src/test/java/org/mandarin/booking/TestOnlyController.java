package org.mandarin.booking;

import java.util.Map;
import org.mandarin.booking.app.persist.MemberQueryRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

//@RestController
//@RequestMapping(path = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
public record TestOnlyController(MemberQueryRepository memberQueryRepository) {

    @PostMapping(path = "/echo", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> echo(@RequestBody Map<String, Object> body) {
        return body;
    }

    @PostMapping(path = "/member/exists", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Boolean exists(@RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        return userId != null && memberQueryRepository.existsByUserId(userId);
    }
}
