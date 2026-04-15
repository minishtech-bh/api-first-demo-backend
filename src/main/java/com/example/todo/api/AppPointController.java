package com.example.todo.api;

import com.example.todo.model.AppPointBalanceResponse;
import com.example.todo.model.TodoDemoPointManagementWalletStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
public class AppPointController implements AppPointsApi {

    @Override
    public ResponseEntity<AppPointBalanceResponse> appPointsApiGetMyBalance() {
        AppPointBalanceResponse response = new AppPointBalanceResponse();
        response.setBalance(12_500L);
        response.setWalletStatus(TodoDemoPointManagementWalletStatus.ACTIVE);
        response.setExpiresAt(OffsetDateTime.now().plusMonths(6));
        return ResponseEntity.ok(response);
    }
}
