package github.io.api_voting_challenge.fixtures;

import java.time.LocalDateTime;

public class TestConstants {
    public static final Long VALID_ID = 1L;
    public static final Long INVALID_ID = 99L;

    public static final Long VALID_SESSION_ID = 1L;
    public static final Long INVALID_SESSION_ID = 99L;
    public static final Long VALID_USER_ID = 1L;

    public static final String VALID_CPF = "130.399.160-84";
    public static final String VALID_CPF_UPDATE = "528.375.080-98";
    public static final String INVALID_CPF = "123.456.789-10";

    public static final String VALID_NAME = "User Test";
    public static final String INVALID_NAME = "user Test";
    public static final String UPDATED_NAME = "User Updated";

    public static final String VALID_AGENDA_TITLE = "title test";
    public static final String VALID_AGENDA_DESCRIPTION = "description test";

    public static final String MESSAGE_ADMIN_NOT_FOUND = "User not found";
    public static final int UPDATED_AGENDAS_COUNT = 2;

    private TestConstants() {}
}
