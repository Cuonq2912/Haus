package com.example.haus.helper;

import com.example.haus.domain.dto.request.user.profile.UpdateUserRequestDto;
import org.springframework.stereotype.Component;

@Component
public class PersonalInformationHelper {

    public UpdateUserRequestDto handleEmptyStrings(
            UpdateUserRequestDto personalInformation) {
        if (personalInformation == null) {
            return null;
        }

        if (personalInformation.getFirstName() != null && personalInformation.getFirstName().trim().isEmpty()) {
            personalInformation.setFirstName(null);
        }

        if (personalInformation.getLastName() != null && personalInformation.getLastName().trim().isEmpty()) {
            personalInformation.setLastName(null);
        }

        if (personalInformation.getPhone() != null && personalInformation.getPhone().trim().isEmpty()) {
            personalInformation.setPhone(null);
        }

        if (personalInformation.getNationality() != null && personalInformation.getNationality().trim().isEmpty()) {
            personalInformation.setNationality(null);
        }

        if(personalInformation.getEmail() != null && personalInformation.getEmail().trim().isEmpty()) {
            personalInformation.setEmail(null);
        }

        return personalInformation;
    }

}
