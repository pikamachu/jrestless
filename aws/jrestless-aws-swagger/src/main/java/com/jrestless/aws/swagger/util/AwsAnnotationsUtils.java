/*
 * Copyright 2016 Bjoern Bilger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jrestless.aws.swagger.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;

import org.springframework.core.annotation.AnnotationUtils;

import com.jrestless.aws.annotation.Cors;
import com.jrestless.aws.annotation.StatusCodes;

/**
 * Utility class handling with AWS specific annotation from 'jrestless-aws-core'
 * and other JAX-RS annotations.
 * <p>
 * Unless noted otherwise meta-annotations are supported.
 *
 * @author Bjoern Bilger
 *
 */
public final class AwsAnnotationsUtils {

	private AwsAnnotationsUtils() {
		// no instance
	}

	/**
	 * Checks if an endpoint is secured.
	 * <p>
	 * An endpoint is secured iff
	 * <ol>
	 * <li>it is annotated with {@link DenyAll}
	 * <li>it is annotated with {@link RolesAllowed}
	 * <li>it is <b>not</b> annotated with {@link PermitAll}
	 * </ol>
	 * <p>
	 * In case the endpoint is not annotated, the resource class is checked. The
	 * endpoint is then secured iff the resource class is annotated with
	 * <ol>
	 * <li>{@link DenyAll}
	 * <li>{@link RolesAllowed}
	 * </ol>
	 *
	 * @param endpointMethod
	 * @return
	 */
	public static boolean isSecured(Method endpointMethod) {
		if (findAnnotationOnMethod(endpointMethod, DenyAll.class).isPresent()) {
			return true;
		}
		if (findAnnotationOnMethod(endpointMethod, RolesAllowed.class).isPresent()) {
			return true;
		}
		if (findAnnotationOnMethod(endpointMethod, PermitAll.class).isPresent()) {
			return false;
		}
		Class<?> resourceClass = endpointMethod.getDeclaringClass();
		if (findAnnotationOnClass(resourceClass, DenyAll.class).isPresent()) {
			return true;
		}
		if (findAnnotationOnClass(resourceClass, RolesAllowed.class).isPresent()) {
			return true;
		}
		return false; // incl. PermitAll
	}

	/**
	 * Checks if CORS is enabled on the endpoint.
	 * <p>
	 * Returns the value of {@link Cors#enabled()} - either from the endpoint
	 * method, the resource class or the annotation's default value.
	 *
	 * @param endpointMethod
	 * @return
	 */
	public static boolean isCorsEnabledOrDefault(Method endpointMethod) {
		return findAnnotationOnMethodOrClass(endpointMethod, Cors.class)
				.orElse(DefaultCors.class.getAnnotation(Cors.class)).enabled();
	}

	/**
	 * Checks if CORS is enabled on the endpoint.
	 * <p>
	 * Returns the value of {@link Cors#enabled()} - either from the endpoint
	 * method, the resource class or the passed default value.
	 *
	 * @param endpointMethod
	 * @param defaultValue
	 * @return
	 */
	public static boolean isCorsEnabledOrDefault(Method endpointMethod, boolean defaultValue) {
		Optional<Cors> corsAnnotation = findAnnotationOnMethodOrClass(endpointMethod, Cors.class);
		if (corsAnnotation.isPresent()) {
			return corsAnnotation.get().enabled();
		} else {
			return defaultValue;
		}
	}

	/**
	 * Returns the endpoint's default status code.
	 * <p>
	 * Returns the value of {@link StatusCodes#defaultCode()} - either from the
	 * endpoint method, the resource class or the annotation's default value.
	 *
	 * @param endpointMethod
	 * @param defaultValue
	 * @return
	 */
	public static int getDefaultStatusCodeOrDefault(Method endpointMethod) {
		return findAnnotationOnMethodOrClass(endpointMethod, StatusCodes.class)
				.orElse(DefaultStatusCodes.class.getAnnotation(StatusCodes.class)).defaultCode();
	}

	/**
	 * Returns the endpoint's additional status codes.
	 * <p>
	 * Returns the value of {@link StatusCodes#defaultCode()} and
	 * {@link StatusCodes#additionalCodes()} - either from the endpoint method,
	 * the resource class or the annotation's default value.
	 *
	 * @param endpointMethod
	 * @param defaultValue
	 * @return
	 */
	public static int[] getAdditionalStatusCodesOrDefault(Method endpointMethod) {
		return findAnnotationOnMethodOrClass(endpointMethod, StatusCodes.class)
				.orElse(DefaultStatusCodes.class.getAnnotation(StatusCodes.class)).additionalCodes();
	}

	/**
	 * Returns the endpoint's default status code and additional status codes.
	 * <p>
	 * Returns the value of {@link StatusCodes#additionalCodes()} - either from the
	 * endpoint method, the resource class or the annotation's default value.
	 *
	 * @param endpointMethod
	 * @param defaultValue
	 * @return
	 */
	public static int[] getAllStatusCodesOrDefault(Method endpointMethod) {
		int[] defaultStatusCodes = getAdditionalStatusCodesOrDefault(endpointMethod);
		int[] statusCodes = new int[defaultStatusCodes.length + 1];
		System.arraycopy(defaultStatusCodes, 0, statusCodes, 1, defaultStatusCodes.length);
		statusCodes[0] = getDefaultStatusCodeOrDefault(endpointMethod);
		return statusCodes;
	}

	private static <T extends Annotation> Optional<T> findAnnotationOnMethodOrClass(Method endpointMethod,
			Class<T> annotationType) {
		Optional<T> statusCodesAnnotation = findAnnotationOnMethod(endpointMethod, annotationType);
		if (!statusCodesAnnotation.isPresent()) {
			statusCodesAnnotation = findAnnotationOnClass(endpointMethod.getDeclaringClass(), annotationType);
		}
		return statusCodesAnnotation;
	}

	private static <T extends Annotation> Optional<T> findAnnotationOnMethod(Method endpointMethod,
			Class<T> annotationType) {
		return Optional.ofNullable(AnnotationUtils.findAnnotation(endpointMethod, annotationType));
	}

	private static <T extends Annotation> Optional<T> findAnnotationOnClass(Class<?> resourceClass,
			Class<T> annotationType) {
		return Optional.ofNullable(AnnotationUtils.findAnnotation(resourceClass, annotationType));
	}

	@StatusCodes
	private static class DefaultStatusCodes {

	}

	@Cors
	private static class DefaultCors {

	}
}
