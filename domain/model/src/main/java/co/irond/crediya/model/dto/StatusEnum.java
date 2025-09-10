package co.irond.crediya.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusEnum {
    PENDING_REVIEW(1, "Pendiete por revisión"),
    REJECTED(2, "Rechazada"),
    MANUAL_REVIEW(3, "Revisión Manual"),
    APPROVED(4, "Aprobada");

    private final long id;
    private final String name;

    public static StatusEnum getById(long id) {
        for (StatusEnum status : values()) {
            if (status.id == id) {
                return status;
            }
        }
        return null;
    }
}
