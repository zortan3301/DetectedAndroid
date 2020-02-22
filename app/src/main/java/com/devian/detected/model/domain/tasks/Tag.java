package com.devian.detected.model.domain.tasks;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Tag {
    private String tagId;
    private int type;
    private float latitude;
    private float longitude;
}
