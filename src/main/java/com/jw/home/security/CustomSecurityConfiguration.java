/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jw.home.security;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

@EnableWebSecurity
public class CustomSecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http
			.csrf().disable()
			.authorizeRequests((authorizeRequests) -> 
				authorizeRequests
					// TODO Authorize
					.antMatchers( "/actuator/**").permitAll()
					.antMatchers(HttpMethod.GET, "/websocket/connect").permitAll()
					.antMatchers(HttpMethod.PUT, "/api/v1/devices/control").permitAll()
					.anyRequest().authenticated()
			)
			.oauth2ResourceServer(oauth -> {
				oauth.jwt();
				DefaultBearerTokenResolver tokenResolver = new DefaultBearerTokenResolver();
				tokenResolver.setAllowFormEncodedBodyParameter(true);
				tokenResolver.setAllowUriQueryParameter(true);
				oauth.bearerTokenResolver(tokenResolver);
			});
		// @formatter:on
	}

	@Bean
	JwtDecoder jwtDecoder() {
		byte[] key = "jwt_test_sign_key".getBytes();
		byte[] paddedKey = key.length < 32 ? Arrays.copyOf(key, 32) : key;

		SecretKey signKey = new SecretKeySpec(paddedKey, "HS256");
		return NimbusJwtDecoder.withSecretKey(signKey).build();
	}
}
