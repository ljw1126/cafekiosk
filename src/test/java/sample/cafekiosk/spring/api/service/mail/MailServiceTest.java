package sample.cafekiosk.spring.api.service.mail;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import sample.cafekiosk.spring.client.mail.MailSendClient;
import sample.cafekiosk.spring.domain.history.mail.MailSendHistory;
import sample.cafekiosk.spring.domain.history.mail.MailSendHistoryRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Spy
    private MailSendClient mailSendClient;

    @Mock
    private MailSendHistoryRepository mailSendHistoryRepository;

    @InjectMocks
    private MailService mailService;

    // 순수 Mockito Test
    @DisplayName("메일 전송 테스트")
    @Test
    void sendMail() {
        // given (MailService 에서 주입 받는 객체를 순수 Mockito 객체로 생성
        // @Mock stubbing
//        Mockito.when(mailSendClient.sendEmail(anyString(), anyString(), anyString(), anyString()))
//                .thenReturn(true); // any(String.class) String 값 아무거나 상관없음

        // @Spy 로 Stubbing
        doReturn(true)
                .when(mailSendClient)
                .sendEmail(anyString(), anyString(), anyString(), anyString());

        // when
        boolean result = mailService.sendMail("", "", "", "");

        // then
        Assertions.assertThat(result).isTrue();
        // 해당 repository 에 save 행위가 times(1) 한번 불렸는지 검증
        Mockito.verify(mailSendHistoryRepository, Mockito.times(1)).save(any(MailSendHistory.class));
    }
}