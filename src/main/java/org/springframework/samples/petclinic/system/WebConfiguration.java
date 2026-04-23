package org.springframework.samples.petclinic.system;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.time.Duration;
import java.util.Locale;

@Configuration
@SuppressWarnings("unused")
public class WebConfiguration implements WebMvcConfigurer {

	@Bean
	public LocaleResolver localeResolver() {
		CookieLocaleResolver resolver = new CookieLocaleResolver("PREFERRED_LANGUAGE");
		resolver.setDefaultLocale(Locale.ENGLISH);
		resolver.setCookieMaxAge(Duration.ofDays(365));
		return resolver;
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
		interceptor.setParamName("lang");
		return interceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
		registry.addInterceptor(new TrailingSlashInterceptor());
	}

	/**
	 * Redirects any request with a trailing slash to the same path without it.
	 * e.g. /games/ -> /games
	 */
	static class TrailingSlashInterceptor implements HandlerInterceptor {

		@Override
		public boolean preHandle(HttpServletRequest request,
								 HttpServletResponse response,
								 Object handler) throws Exception {
			String path = request.getRequestURI();
			if (path.length() > 1 && path.endsWith("/")) {
				String queryString = request.getQueryString();
				String redirectUrl = path.substring(0, path.length() - 1);
				if (queryString != null) {
					redirectUrl += "?" + queryString;
				}
				response.sendRedirect(redirectUrl);
				return false;
			}
			return true;
		}
	}
}
