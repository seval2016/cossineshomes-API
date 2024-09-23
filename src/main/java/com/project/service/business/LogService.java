package com.project.service.business;

import com.project.entity.concretes.business.Advert;
import com.project.entity.concretes.user.User;
import com.project.entity.enums.Log;
import com.project.repository.business.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {

        private final LogRepository logRepository;

        public com.project.entity.concretes.business.Log createLogEvent(User user, Advert advert, Log log) {
            com.project.entity.concretes.business.Log logReport = com.project.entity.concretes.business.Log.builder()
                    .log(log)
                    .advert(advert)
                    .user(user)
                    .build();
            return logRepository.save(logReport);
        }

        public void resetLogTables() {
            logRepository.deleteAll();
        }
    }
