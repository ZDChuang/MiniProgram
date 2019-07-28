package com.dech.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:hupu.properties")
public class HupuConfigure {
	@Value("${pages}")
	public int pages;
}
