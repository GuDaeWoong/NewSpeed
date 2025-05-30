package com.example.newspeed.global.error;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FilterException {

    //Filter 는 Spring 예외처리의 범위 밖이기 때문에 Global 예외 처리로 잡을 수 없다
    //직접 수동으로 메세지를 남겨준다.
    //세션에 없을 시 예외 처리. 예외 메세지는 Json 형식으로 반환
    public void writeExceptionResponse(HttpServletResponse httpResponse) throws IOException {
        int status = 400;
        String responseMessage = String.format("{\"status\": %d, \"message\": \"%s\"}",status, "잘못된 접근입니다.");

        httpResponse.setStatus(status);
        httpResponse.setContentType("application/json;charset=UTF-8");
        httpResponse.getWriter().write(responseMessage);
    }
}
