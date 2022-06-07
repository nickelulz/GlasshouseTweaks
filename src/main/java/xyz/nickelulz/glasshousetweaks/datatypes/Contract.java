package xyz.nickelulz.glasshousetweaks.datatypes;

import java.time.LocalDateTime;

public class Contract extends Hit {
    private User contractor;
    private boolean pending;

    /**
     * Unclaimed Hit Constructor (New contract)
     * @param placer Player that placed this contract
     * @param target The target of this contract
     * @param price The price of this contract
     * @param timePlaced When the contract was placed initially
     * @param contractor The contractor of this contract
     * @param pending Whether this hit has been accepted yet
     */
    public Contract(User placer, User target, int price, LocalDateTime timePlaced, User contractor, boolean pending) {
        super(placer, target, price, timePlaced);
        this.contractor = contractor;
        this.pending = pending;
    }

    /**
     * Claimed Hit Constructor (Completed contract)
     * @param placer Player that placed this contract
     * @param target The target of this contract
     * @param price The price of this contract
     * @param timePlaced When the contract was placed initially
     * @param claimer The player that claimed this contract
     * @param timeClaimed When this contract was claimed
     * @param contractor The contractor of this contract
     */
    public Contract(User placer, User target, int price, LocalDateTime timePlaced, User claimer, LocalDateTime timeClaimed, User contractor) {
        super(placer, target, price, timePlaced, claimer, timeClaimed);
        this.contractor = contractor;
        this.pending = false;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" CONTRACTOR: %s, %s", contractor.getProfile().getName(),
                (pending) ? "PENDING" : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Contract))
            return false;
        Contract other = (Contract) o;
        return super.equals(other) &&
                contractor.equals(other.contractor) &&
                pending == other.pending;
    }

    /**
     * GETTERS AND SETTERS
     */
    public User getContractor() {
        return contractor;
    }

    public void setContractor(User contractor) {
        this.contractor = contractor;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }
}
