package ru.covid.app.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import ru.covid.app.exception.HttpCodeException;
import ru.covid.app.service.auth.AuthService;

@Component
@RequiredArgsConstructor
public class ControllerHelper {

    private final static Logger log = LoggerFactory.getLogger(ControllerHelper.class);

    private final AuthService authService;

    public void handleToken(String header) {
        var token = header.replace("Bearer ", "").trim();
        try {
            var uuid = authService.getUuidFromToken(token);
            MDC.put("user", uuid);
            log.info("ControllerHelper.obtainToken Request by user with uuid = {}", uuid);
        } catch (HttpCodeException e) {
            log.error("ControllerHelper.obtainToken.thrown", e);
            throw e;
        } catch (Exception e) {
            log.error("ControllerHelper.obtainToken.thrown Error while obtaining token", e);
        }
    }
}
