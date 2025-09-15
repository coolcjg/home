package com.cjg.home.document;

import jakarta.persistence.Id;
import lombok.Builder;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "post")
@Builder
public record PostDoc(
        @Id
        String id,

        @Indexed
        String userId,

        String title,
        String content,
        char open
) {}