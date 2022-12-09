package josegamerpt.realscoreboard.api.managers;

/**
 * Abstraction class for AnimationManager
 */
public abstract class AbstractAnimationManager {

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
    public abstract void reload();
}