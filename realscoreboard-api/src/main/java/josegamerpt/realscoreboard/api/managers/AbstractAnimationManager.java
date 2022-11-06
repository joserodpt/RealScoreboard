package josegamerpt.realscoreboard.api.managers;

/**
 * Abstraction class for AnimationManager
 */
public abstract class AbstractAnimationManager {

    public abstract void start();

    public abstract String getLoopAnimation(String s);

    public abstract void stop();

    public abstract void cancelAnimationTasks();

    public abstract void reload();
}
