package com.bitstudy.app.domain.type;

import lombok.Getter;

/** 저장과 수정 요청여부를 판단해서 '저장' 을 보여주던 '수정' 을 보여주던 함. */

public enum FormStatus {

    CREATE("저장", false),
    UPDATE("수정", true);

    @Getter private final String description;
    @Getter private final Boolean update;

    FormStatus(String description, Boolean update) {
        this.description = description;
        this.update = update;
    }

}
