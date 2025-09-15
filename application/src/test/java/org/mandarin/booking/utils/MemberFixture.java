package org.mandarin.booking.utils;

import java.util.UUID;

public class MemberFixture {
    public static class EmailGenerator {
        private static final String EMAIL_DOMAIN = "@gmail.com";

        public static String generateEmail() {
            return UUID.randomUUID() + EMAIL_DOMAIN;
        }
    }

    public static class NicknameGenerator {
        public static String generateNickName() {
            return UUID.randomUUID().toString();
        }
    }

    public static class PasswordGenerator {
        public static String generatePassword() {
            return UUID.randomUUID().toString();
        }
    }

    public static class UserIdGenerator {
        public static String generateUserId() {
            return UUID.randomUUID().toString();
        }
    }
}
