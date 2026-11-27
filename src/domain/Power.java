package domain;

import java.io.Serializable;

/**
 * Power shot of a character.
 */
public interface Power extends Serializable {

    /** Returns the name of the power. */
    String getName();

    /** Returns the damage of the shot. */
    double getDamage();

    /** Returns how many frames the effect lasts on the rival. */
    int getDuration();

    /** Returns how many fake balls the power makes. */
    int getFakeBalls();

    /** Makes the ball go out as this power shot. */
    void shoot(Ball ball, int direction);

    /** Effect on the rival, called every frame while it lasts. */
    void affect(Player rival, int direction);
}
