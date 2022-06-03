package xyz.nickelulz.glasshousetweaks.datatypes;

import java.time.LocalDateTime;

public class Bounty extends Hit {
    public Bounty(User placer, User target, int price, LocalDateTime timePlaced) {
        super(placer, target, price, timePlaced);
    }

    public Bounty(User placer, User target, int price, LocalDateTime timePlaced, User claimer, LocalDateTime timeClaimed) {
        super(placer, target, price, timePlaced, claimer, timeClaimed);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Bounty))
            return false;
        Bounty other = (Bounty) o;
        return super.equals(other);
    }
}
