package com.interviewbridge.service;

import com.interviewbridge.request.UserProfileSetupRequest;
import com.interviewbridge.response.UserProfileResponse;

/**
 * Service interface for managing user profile details.
 */
public interface UserProfileService {

    /**
     * Sets up technology and experience mappings for the logged-in user profile.
     *
     * @param email the user email from security context
     * @param request the profile setup details
     * @return the updated user profile response
     */
    UserProfileResponse setupProfile(String email, UserProfileSetupRequest request);

    /**
     * Updates technology and experience mappings for the logged-in user profile.
     *
     * @param email the user email from security context
     * @param request the profile update details
     * @return the updated user profile response
     */
    UserProfileResponse updateProfile(String email, UserProfileSetupRequest request);

    /**
     * Retrieves the profile details of the logged-in user.
     *
     * @param email the user email from security context
     * @return the user profile response
     */
    UserProfileResponse getProfile(String email);
}
