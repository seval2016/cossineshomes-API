package com.project.service.business;

import com.project.entity.concretes.business.Advert;
import com.project.entity.concretes.user.User;
import com.project.entity.enums.LogEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {
    public void createLogEvent(User user, Advert advert, LogEnum logEnum) {
    }
}
