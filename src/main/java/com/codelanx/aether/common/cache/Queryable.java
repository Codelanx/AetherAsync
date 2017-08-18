package com.codelanx.aether.common.cache;

import com.codelanx.aether.common.cache.query.Inquiry;

public interface Queryable<R extends Inquiry> {

    public R toInquiry();
}
