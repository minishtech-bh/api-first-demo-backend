package com.example.todo.api;

import com.example.todo.model.TodoDemoPointManagementCreateTransactionRequest;
import com.example.todo.model.TodoDemoPointManagementPointDetailResponse;
import com.example.todo.model.TodoDemoPointManagementPointSummaryPage;
import com.example.todo.model.TodoDemoPointManagementPointSummaryResponse;
import com.example.todo.model.TodoDemoPointManagementPointTransactionPage;
import com.example.todo.model.TodoDemoPointManagementPointTransactionResponse;
import com.example.todo.model.TodoDemoPointManagementPointTransactionType;
import com.example.todo.model.TodoDemoPointManagementTransactionDirection;
import com.example.todo.model.TodoDemoPointManagementWalletStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
public class AdminPlatformPointController implements AdminPlatformPointsApi {

    private static final List<TodoDemoPointManagementPointSummaryResponse> MOCK_LIST = List.of(
        mockSummary(1L, "김철수", "대리", 101L, "스타벅스", 8_000L, TodoDemoPointManagementWalletStatus.ACTIVE),
        mockSummary(2L, "이영희", "과장", 101L, "스타벅스", 15_000L, TodoDemoPointManagementWalletStatus.ACTIVE),
        mockSummary(3L, "박민준", "사원", 202L, "CJ푸드빌", 0L, TodoDemoPointManagementWalletStatus.FROZEN),
        mockSummary(4L, "최지은", "차장", 202L, "CJ푸드빌", 32_000L, TodoDemoPointManagementWalletStatus.ACTIVE)
    );

    @Override
    public ResponseEntity<TodoDemoPointManagementPointSummaryPage> adminPlatformPointsApiList(
            Integer page, Integer size, String employeeName, TodoDemoPointManagementWalletStatus status) {
        TodoDemoPointManagementPointSummaryPage result = new TodoDemoPointManagementPointSummaryPage(
            MOCK_LIST, (long) MOCK_LIST.size(), 1, 10, 0, true, true
        );
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<TodoDemoPointManagementPointDetailResponse> adminPlatformPointsApiGet(Long employeeInfoId) {
        TodoDemoPointManagementPointDetailResponse detail = new TodoDemoPointManagementPointDetailResponse();
        detail.setEmployeeInfoId(employeeInfoId);
        detail.setEmployeeName("김철수");
        detail.setPositionName("대리");
        detail.setPartnerId(101L);
        detail.setPartnerName("스타벅스");
        detail.setBalance(8_000L);
        detail.setStatus(TodoDemoPointManagementWalletStatus.ACTIVE);
        detail.setExpiresAt(OffsetDateTime.now().plusMonths(3));
        detail.setEmploymentStatus("EMPLOYED");
        detail.setTotalGranted(20_000L);
        detail.setTotalUsed(10_000L);
        detail.setTotalExpired(2_000L);
        detail.setTotalRecalled(0L);
        return ResponseEntity.ok(detail);
    }

    @Override
    public ResponseEntity<TodoDemoPointManagementPointTransactionPage> adminPlatformPointsApiListTransactions(
            Long employeeInfoId, Integer page, Integer size, TodoDemoPointManagementPointTransactionType type) {
        throw new UnsupportedOperationException("구현 필요");
    }

    @Override
    public ResponseEntity<TodoDemoPointManagementPointTransactionResponse> adminPlatformPointsApiCreateTransaction(
            Long employeeInfoId, TodoDemoPointManagementCreateTransactionRequest request) {
        TodoDemoPointManagementPointTransactionResponse tx = new TodoDemoPointManagementPointTransactionResponse();
        tx.setId(1L);
        tx.setType(TodoDemoPointManagementPointTransactionType.fromValue(request.getType().getValue()));
        tx.setDirection(TodoDemoPointManagementTransactionDirection.CREDIT);
        tx.setAmount(request.getAmount());
        tx.setBalanceAfter(8_000L + request.getAmount());
        tx.setReason(request.getReason());
        tx.setCreatedAt(OffsetDateTime.now());
        return ResponseEntity.status(201).body(tx);
    }

    private static TodoDemoPointManagementPointSummaryResponse mockSummary(
            Long id, String name, String position, Long partnerId, String partnerName,
            Long balance, TodoDemoPointManagementWalletStatus status) {
        TodoDemoPointManagementPointSummaryResponse s = new TodoDemoPointManagementPointSummaryResponse();
        s.setEmployeeInfoId(id);
        s.setEmployeeName(name);
        s.setPositionName(position);
        s.setPartnerId(partnerId);
        s.setPartnerName(partnerName);
        s.setBalance(balance);
        s.setStatus(status);
        s.setExpiresAt(OffsetDateTime.now().plusMonths(3));
        return s;
    }
}
