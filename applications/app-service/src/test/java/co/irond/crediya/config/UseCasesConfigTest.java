package co.irond.crediya.config;

import co.irond.crediya.model.application.gateways.ApplicationRepository;
import co.irond.crediya.model.debtcapacity.DebtCapacityGateway;
import co.irond.crediya.model.loantype.gateways.LoanTypeRepository;
import co.irond.crediya.model.notification.NotificationGateway;
import co.irond.crediya.model.reports.ReportGateway;
import co.irond.crediya.model.status.gateways.StatusRepository;
import co.irond.crediya.model.user.UserGateway;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            String[] beanNames = context.getBeanDefinitionNames();

            boolean useCaseBeanFound = false;
            for (String beanName : beanNames) {
                if (beanName.endsWith("UseCase")) {
                    useCaseBeanFound = true;
                    break;
                }
            }

            assertTrue(useCaseBeanFound, "No beans ending with 'Use Case' were found");
        }
    }

    @Configuration
    @Import(UseCasesConfig.class)
    static class TestConfig {

        @Bean
        public MyUseCase myUseCase() {
            return new MyUseCase();
        }

        @Bean
        public ApplicationRepository applicationRepository() {
            return Mockito.mock(ApplicationRepository.class);
        }

        @Bean
        public LoanTypeRepository loanTypeRepository() {
            return Mockito.mock(LoanTypeRepository.class);
        }

        @Bean
        public StatusRepository statesRepository() {
            return Mockito.mock(StatusRepository.class);
        }

        @Bean
        public UserGateway userGateway() {
            return Mockito.mock(UserGateway.class);
        }

        @Bean
        public NotificationGateway notificationGateway() {
            return Mockito.mock(NotificationGateway.class);
        }

        @Bean
        public DebtCapacityGateway debtCapacityGateway() {
            return Mockito.mock(DebtCapacityGateway.class);
        }

        @Bean
        public ReportGateway reportGateway() {
            return Mockito.mock(ReportGateway.class);
        }
    }

    static class MyUseCase {
        public String execute() {
            return "MyUseCase Test";
        }
    }
}