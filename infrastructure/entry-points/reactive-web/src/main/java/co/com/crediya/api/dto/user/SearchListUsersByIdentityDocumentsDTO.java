package co.com.crediya.api.dto.user;

import java.util.List;

public record SearchListUsersByIdentityDocumentsDTO(
        List<String> identityDocuments
) { }