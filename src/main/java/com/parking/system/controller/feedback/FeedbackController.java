package com.parking.system.controller.feedback;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasAnyRole('DRIVER','STAFF','MANAGER','SYSTEM_ADMIN')")
public class FeedbackController {
}
