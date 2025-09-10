//
//    @PutMapping
//    public UserDTO updateProfile(@AuthenticationPrincipal UserDetails userDetails,
//                                 @RequestBody UpdateProfileRequest request) {
//        return userService.updateProfile(userDetails.getUsername(), request);
//    }
//
//    @PostMapping("/change-password")
//    public ResponseEntity<String> changePassword(@AuthenticationPrincipal UserDetails userDetails,
//                                                 @RequestBody ChangePasswordRequest request) {
//        userService.changePassword(userDetails.getUsername(), request);
//        return ResponseEntity.ok("Password changed successfully");
//    }
}
