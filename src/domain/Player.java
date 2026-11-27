package domain;

import java.io.Serializable;

/**
 * Player of a match. It can move, jump, kick and use its power.
 */
public abstract class Player implements Serializable {

    private String name;
    private double posX;          //Centro del jugador
    private double posY;          //0 es el suelo
    private double speedX;
    private double speedY;
    private double width;
    private double height;
    private double speed;         //Velocidad al caminar, sube con la mejora Speed
    private double jumpForce;
    private double kickForce;
    private boolean inAir;
    private int energyPower;      //Nivel de la barra de poder, de 0 a maxPower
    private int maxPower;
    private int framesPerLevel;   //Cuadros para subir un nivel de la barra
    private Character character;
    private int kickDuration;     //Cuadros que se muestra la imagen de patear
    private int kickFrames;       //Cuadros que faltan de la patada
    private boolean kickHit;      //Es true si la patada actual ya le pego al rival
    private int facing;           //Hacia donde mira: 1 derecha, -1 izquierda
    private int powerFrames;      //Cuadros contados para el siguiente nivel
    private boolean aura;         //Es true despues de usar el poder hasta que toca el balon
    private double life;
    private double maxLife;
    private int kick;             //Estadistica de patada, de 1 a 10
    private Power effect;         //Poder del rival que lo esta afectando, o null
    private int effectFrames;     //Cuadros que faltan del efecto
    private int effectDirection;
    private int stunDuration;
    private int stunFrames;       //Cuadros que faltan del aturdimiento

    /** Creates a player on the ground at posX. */
    public Player(String name, Character character, double posX){
        this.name = name;
        this.character = character;
        this.posX = posX;
        this.posY = 0;
        this.speedX = 0;
        this.speedY = 0;
        this.width = 120;
        this.height = 200;
        this.speed = character.getBaseSpeed();
        this.jumpForce = character.getBaseJump();
        this.kickForce = character.getBaseForce();
        this.inAir = false;
        this.energyPower = 0;
        this.maxPower = 5;
        this.framesPerLevel = 120;  //La barra se llena en 10 segundos, un nivel cada 2 segundos
        this.kickDuration = 12;
        this.kickFrames = 0;
        this.kickHit = false;
        this.facing = 1;
        this.powerFrames = 0;
        this.aura = false;
        this.maxLife = 100;
        this.life = maxLife;
        this.kick = 1;
        this.effect = null;
        this.effectFrames = 0;
        this.effectDirection = 0;
        this.stunDuration = 180;  //3 segundos
        this.stunFrames = 0;
    }

    /** Applies the upgrades bought in the shop. */
    public void applyStats(Stats stats){
        //Cada nivel por encima de 1 suma un porcentaje sobre el valor base
        //Speed: 15% mas rapido por nivel
        speed = character.getBaseSpeed() * (1 + 0.15 * (stats.getLevel("Speed") - 1));
        //Jump: 6% mas fuerza de salto por nivel, se nota mucho porque la altura crece mas rapido
        jumpForce = character.getBaseJump() * (1 + 0.06 * (stats.getLevel("Jump") - 1));
        //Kick: se usa en getKickMultiplier, patadas mas fuertes y poderes con mas dano
        kick = stats.getLevel("Kick");
        //Survival: 20% mas vida por nivel
        maxLife = 100 * (1 + 0.20 * (stats.getLevel("Survival") - 1));
        life = maxLife;
    }

    /** Starts moving to the left. */
    public void moveLeft(){
        speedX = -speed;
        facing = -1;
    }

    /** Starts moving to the right. */
    public void moveRight(){
        speedX = speed;
        facing = 1;
    }

    /** Stops moving. */
    public void stop(){
        speedX = 0;
    }

    /** Puts the player back on the ground at posX, still. */
    public void restart(double posX){
        //Se usa despues de un gol, tambien se le quita el efecto que tenga
        this.posX = posX;
        this.posY = 0;
        this.speedX = 0;
        this.speedY = 0;
        this.inAir = false;
        this.kickFrames = 0;
        this.effect = null;
        this.effectFrames = 0;
    }

    /** Jumps if the player is on the ground. */
    public void jump(){
        //No se puede saltar estando en el aire
        if (!inAir) {
            speedY = jumpForce;
            inAir = true;
        }
    }

    /** Starts a kick. */
    public void kick(){
        //Solo empieza otra patada cuando termino la anterior
        if (kickFrames == 0) {
            kickFrames = kickDuration;
            kickHit = false;
        }
    }

    /** Advances the player one frame. */
    public void update(double gravity){
        //Primero se aplican los efectos, porque pueden cambiar la velocidad
        applyEffect();
        applyStun();
        posX += speedX;
        posY += speedY;
        if (inAir) {
            speedY -= gravity;
        }
        //Cuando llega al suelo se queda ahi
        if (posY <= 0) {
            posY = 0;
            speedY = 0;
            inAir = false;
        }
        if (kickFrames > 0) {
            kickFrames--;
        }
        chargePower();
    }

    /** Charges the power bar. */
    private void chargePower(){
        if (energyPower == maxPower) {
            return;
        }
        //Cada framesPerLevel cuadros sube un nivel
        powerFrames++;
        if (powerFrames == framesPerLevel) {
            powerFrames = 0;
            energyPower++;
        }
    }

    /** Adds a quarter of the power bar, because the player received a goal. */
    public void receiveGoal(){
        int quarter = maxPower * framesPerLevel / 4;
        //Se cuenta la barra en cuadros para poder sumar cuartos sin perder lo que ya habia
        int total = energyPower * framesPerLevel + powerFrames + quarter;
        if (total >= maxPower * framesPerLevel) {
            energyPower = maxPower;
            powerFrames = 0;
        } else {
            energyPower = total / framesPerLevel;
            powerFrames = total % framesPerLevel;
        }
    }

    /** Tells if the power bar is full. */
    public boolean hasFullPower(){
        return energyPower == maxPower;
    }

    /** Keeps the player inside the field. */
    public void keepInside(double minX, double maxX){
        //Si se paso de un borde lo devuelve al borde
        double half = width / 2;
        if (posX - half < minX) {
            posX = minX + half;
            speedX = 0;
        }
        if (posX + half > maxX) {
            posX = maxX - half;
            speedX = 0;
        }
    }

    /** Tells the player which match it plays. Only the machine uses it. */
    public void watch(Match match){
    }

    /** Decides what to do in this frame. */
    public abstract void decide();

    /** A key was pressed. */
    public void pressKey(int key){
        //Por defecto no hace nada, el jugador humano lo cambia
    }

    /** A key was released. */
    public void releaseKey(int key){
    }

    /** Moves the player sideways without changing its speed. */
    public void shiftX(double distance){
        posX += distance;
    }

    /** Tells if the current kick already hit the rival. */
    public boolean hasKickHit(){
        return kickHit;
    }

    /** Remembers that the current kick hit the rival, so it only hurts once. */
    public void markKickHit(){
        kickHit = true;
    }

    /** Returns where the player looks: 1 to the right and -1 to the left. */
    public int getFacing(){
        return facing;
    }

    /** Changes where the player looks. */
    public void setFacing(int facing){
        this.facing = facing;
    }

    /** Tells if the player is kicking. */
    public boolean isKicking(){
        return kickFrames > 0;
    }

    /** Uses the power if the bar is full. */
    public void usePower(){
        //Necesita la barra llena, no tener ya el aura y que su personaje tenga poder
        if (hasFullPower() && !aura && character.getPower() != null) {
            energyPower = 0;
            powerFrames = 0;
            aura = true;
        }
    }

    /** Removes the aura. */
    public void loseAura(){
        aura = false;
    }

    /** Receives the effect of the rival's power. */
    public void receivePower(Power power, int direction){
        //Si ya tenia un efecto, el nuevo lo reemplaza
        effect = power;
        effectFrames = power.getDuration();
        effectDirection = direction;
    }

    /** Takes life away. */
    public void receiveDamage(double damage){
        life = life - damage;
        //La vida nunca baja de 0
        if (life < 0) {
            life = 0;
        }
        //Si llega a 0 queda aturdido, si ya estaba aturdido no se alarga
        if (life == 0 && stunFrames == 0) {
            stunFrames = stunDuration;
        }
    }

    /** Stuns the player while there are stun frames. */
    private void applyStun(){
        if (stunFrames == 0) {
            return;
        }
        //Aturdido: no se mueve, no salta y no patea
        speedX = 0;
        cancelJump();
        cancelKick();
        stunFrames--;
        //Cuando termina el aturdimiento la vida vuelve al maximo
        if (stunFrames == 0) {
            life = maxLife;
        }
    }

    /** Tells if the player is stunned. */
    public boolean isStunned(){
        return stunFrames > 0;
    }

    /** Applies the effect of the rival's power. */
    private void applyEffect(){
        if (effectFrames == 0) {
            return;
        }
        //Cada poder hace su propio efecto
        effect.affect(this, effectDirection);
        effectFrames--;
        if (effectFrames == 0) {
            effect = null;
        }
    }

    /** Returns the damage multiplier of the kick stat. */
    public double getKickMultiplier(){
        //Cada punto de kick por encima de 1 suma un 15% de fuerza y de dano
        return 1 + 0.15 * (kick - 1);
    }

    /** Cancels a jump that is starting. */
    public void cancelJump(){
        //Solo si esta en el suelo, si ya estaba en el aire lo deja caer
        if (posY == 0) {
            speedY = 0;
            inAir = false;
        }
    }

    /** Throws the player up. */
    public void throwUp(double speed){
        speedY = speed;
        inAir = true;
    }

    /** Cancels the kick. */
    public void cancelKick(){
        kickFrames = 0;
    }

    /** Returns the frames left of the effect. */
    public int getEffectFrames(){
        return effectFrames;
    }

    /** Changes the horizontal speed. */
    public void setSpeedX(double speedX){
        this.speedX = speedX;
    }

    /** Tells if the player has the aura. */
    public boolean hasAura(){
        return aura;
    }

    /** Tells if a rival's power is affecting the player. */
    public boolean isUnderEffect(){
        return effectFrames > 0;
    }

    /** Returns the power that is affecting the player. */
    public Power getEffect(){
        return effect;
    }

    /** Returns the life. */
    public double getLife(){
        return life;
    }

    /** Returns the max life. */
    public double getMaxLife(){
        return maxLife;
    }

    /** Returns the name of the image to show. */
    public String getImage(){
        return character.getImage(isKicking());
    }

    /** Returns the name. */
    public String getName(){
        return name;
    }

    /** Returns the x of the center. */
    public double getPosX(){
        return posX;
    }

    /** Returns the y of the feet. */
    public double getPosY(){
        return posY;
    }

    /** Returns the horizontal speed. */
    public double getSpeedX(){
        return speedX;
    }

    /** Returns the vertical speed. */
    public double getSpeedY(){
        return speedY;
    }

    /** Returns the width. */
    public double getWidth(){
        return width;
    }

    /** Returns the height. */
    public double getHeight(){
        return height;
    }

    /** Returns the jump force. */
    public double getJumpForce(){
        return jumpForce;
    }

    /** Returns the kick force. */
    public double getKickForce(){
        return kickForce;
    }

    /** Tells if the player is in the air. */
    public boolean isInAir(){
        return inAir;
    }

    /** Returns the level of the power bar. */
    public int getEnergyPower(){
        return energyPower;
    }

    /** Returns the max level of the power bar. */
    public int getMaxPower(){
        return maxPower;
    }

    /** Returns the frames to go up one level. */
    public int getFramesPerLevel(){
        return framesPerLevel;
    }

    /** Returns the frames of a kick. */
    public int getKickDuration(){
        return kickDuration;
    }

    /** Returns the character. */
    public Character getCharacter(){
        return character;
    }
}
