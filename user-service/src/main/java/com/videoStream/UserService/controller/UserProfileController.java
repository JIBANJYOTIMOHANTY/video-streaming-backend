package com.videoStream.UserService.controller;

import com.videoStream.UserService.model.UserProfile;
import com.videoStream.UserService.repository.UserProfileRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserProfileController {

    private final UserProfileRepository userProfileRepository;

    public UserProfileController(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserProfile> getProfile(@PathVariable String username) {
        UserProfile profile = userProfileRepository.findByUsername(username)
                .orElseGet(() -> {
                    // Create default profile if not exists
                    UserProfile newProfile = UserProfile.builder()
                            .username(username)
                            .channelName(username + "'s Channel")
                            .bio("")
                            .subscribersCount(0)
                            .build();
                    return userProfileRepository.save(newProfile);
                });
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{username}")
    public ResponseEntity<UserProfile> updateProfile(@PathVariable String username, @RequestBody UserProfile profileDetails) {
        UserProfile profile = userProfileRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        profile.setChannelName(profileDetails.getChannelName());
        profile.setBio(profileDetails.getBio());

        UserProfile updatedProfile = userProfileRepository.save(profile);
        return ResponseEntity.ok(updatedProfile);
    }
}
