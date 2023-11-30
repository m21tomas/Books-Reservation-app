package soft.project.demo.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ReservationStatus {
	PENDING("Pending"),      // Book is ordered by the reader but still not confirmed by librarian or admin
    RESERVED("Reserved"),    // Reservation is complete and the book has been borrowed
    RETURNED("Returned"),    // Book has been returned by the user
	REJECTED("Rejected");    // Book was not borrowed by the librarian to visitor
    
    private String status;
	
	ReservationStatus(String status){
		this.status = status;
	}
	
	public String getStatus() {
		return status;
	}
}
