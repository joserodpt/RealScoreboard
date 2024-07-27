package joserodpt.realscoreboard.api.managers;

/*
 *   ____            _ ____                     _                         _
 *  |  _ \ ___  __ _| / ___|  ___ ___  _ __ ___| |__   ___   __ _ _ __ __| |
 *  | |_) / _ \/ _` | \___ \ / __/ _ \| '__/ _ \ '_ \ / _ \ / _` | '__/ _` |
 *  |  _ <  __/ (_| | |___) | (_| (_) | | |  __/ |_) | (_) | (_| | | | (_| |
 *  |_| \_\___|\__,_|_|____/ \___\___/|_|  \___|_.__/ \___/ \__,_|_|  \__,_|
 *
 *
 * Licensed under the MIT License
 * @author José Rodrigues © 2016-2024
 * @link https://github.com/joserodpt/RealScoreboard
 */

/**
 * Abstraction class for AnimationManager
 */
public abstract class AnimationManagerAPI {

    /**
     * Starts all animations
     */
    public abstract void start();

    public abstract String getLoopAnimation(String s);

    /**
     * Stops all animations
     */
    public abstract void stop();

    /**
     * Cancels all animation tasks
     */
    public abstract void cancelAnimationTasks();

    /**
     * Restart animation manager (stop and start again)
     */
    @SuppressWarnings("unused")
    public abstract void reload();
}