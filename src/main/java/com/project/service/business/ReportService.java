package com.project.service.business;

import com.project.entity.concretes.business.Advert;
import com.project.entity.concretes.business.TourRequest;
import com.project.entity.concretes.user.User;
import com.project.entity.enums.AdvertStatus;
import com.project.entity.enums.RoleType;
import com.project.entity.enums.StatusType;
import com.project.exception.BadRequestException;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.response.business.AdvertResponse;
import com.project.repository.business.*;
import com.project.repository.user.UserRepository;
import com.project.service.helper.AdvertHelper;
import com.project.service.helper.MethodHelper;
import com.project.service.helper.TourRequestHelper;
import com.project.service.user.UserService;
import com.project.service.validator.DateTimeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final MethodHelper methodHelper;
    private final CategoryRepository categoryRepository;
    private final AdvertRepository advertRepository;
    private final AdvertTypesRepository advertTypesRepository;
    private final TourRequestRepository tourRequestRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final DateTimeValidator dateTimeValidator;
    private final TourRequestHelper tourRequestHelper;
    private final AdvertHelper advertHelper;

    public ResponseEntity<Map<String, Long>> getStaticts(HttpServletRequest request) {
        User user = methodHelper.getUserByHttpRequest(request);
        methodHelper.checkRoles(user, RoleType.ADMIN, RoleType.MANAGER);
        Map<String, Long> logMap = new HashMap<>();

        Long categoryNo = categoryRepository.count();
        Long advertNo = advertRepository.count();
        Long advertTypeNo = advertTypesRepository.count();
        Long tourRequestNo = tourRequestRepository.count();
        Long userNo = userRepository.count();

        logMap.put("categories", categoryNo);
        logMap.put("adverts", advertNo);
        logMap.put("advertTypes", advertTypeNo);
        logMap.put("tourRequests", tourRequestNo);
        logMap.put("customers", userNo);

        return ResponseEntity.ok(logMap);
    }

    public ResponseEntity<byte[]> getPopulerAdvertsReport(int amount, HttpServletRequest request) {
        User user = methodHelper.getUserByHttpRequest(request);
        methodHelper.checkRoles(user, RoleType.MANAGER, RoleType.ADMIN);

        Pageable pageable = PageRequest.of(0, amount); // Pageable amount parametresini kullanır.
        List<AdvertResponse> adverts = advertHelper.getMostPopularAdverts(pageable);

        return methodHelper.excelResponse(adverts);
    }

    public ResponseEntity<byte[]> getUsersWithRol(String rol, HttpServletRequest request) {

        User user = methodHelper.getUserByHttpRequest(request);
        methodHelper.checkRoles(user, RoleType.MANAGER, RoleType.ADMIN);

        RoleType roleType;
        try {
            roleType = RoleType.valueOf(rol);

        } catch (BadRequestException e) {
            throw new BadRequestException(ErrorMessages.ADVERT_STATUS_NOT_FOUND);
        }

       List<User> users = userService.getUsersByRoleType(roleType);
        return methodHelper.excelResponse(users);


    }

    public ResponseEntity<byte[]> getTourRequest(HttpServletRequest request, String date1, String date2, String status) {

        User user = methodHelper.getUserByHttpRequest(request);
        methodHelper.checkRoles(user, RoleType.ADMIN, RoleType.MANAGER);

        DateTimeFormatter dateFormatter=DateTimeFormatter.ofPattern("dd-MM-yyyy");

        LocalDateTime begin = null;
        LocalDateTime end = null;
        if (date1 != null && !date1.isEmpty()) {
            LocalDate startDate = LocalDate.parse(date1, dateFormatter);
            begin = startDate.atStartOfDay();
        }
        if (date2 != null && !date2.isEmpty()) {
            LocalDate endDate = LocalDate.parse(date2, dateFormatter);
            end = endDate.atTime(LocalTime.MAX);
        }
        if (begin != null && end != null) {
            dateTimeValidator.checkBeginTimeAndEndTime(begin, end);
        }
        StatusType statusType;
        try {
            statusType = StatusType.valueOf(status);
        } catch (BadRequestException e) {
            throw new BadRequestException(ErrorMessages.ADVERT_STATUS_NOT_FOUND);
        }

        List<TourRequest> tourRequests = tourRequestHelper.getTourRequest(begin,end,statusType);
        return methodHelper.excelResponse(tourRequests);
    }

    public ResponseEntity<byte[]> getAdverts(HttpServletRequest request, String date1, String date2, String category, String type, AdvertStatus status) {

        User user = methodHelper.getUserByHttpRequest(request);
        methodHelper.checkRoles(user, RoleType.ADMIN, RoleType.MANAGER);
        List<Advert> adverts = advertHelper.getAdvertsReport(date1, date2, category, type.toLowerCase(), status);

        return methodHelper.excelResponse(adverts);


    }

}
