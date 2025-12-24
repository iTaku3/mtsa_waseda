package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot;

import MTSAEnactment.ar.uba.dc.lafhis.enactment.Enactor;

/**
 * Adaptador para responder ante acciones ligadas a los Robots
 *
 * @author Julio
 */
public abstract class RobotAdapter<State, Action> extends Enactor<State, Action> {

  protected Action success;
  protected Action failure;
  protected Action lost;

  public RobotAdapter(String name, Action success, Action failure, Action lost) {
    super(name);

    this.success = success;
    this.failure = failure;
    this.lost = lost;
  }

  public RobotAdapter(Action success, Action failure, Action lost) {
    this("default adapter", success, failure, lost);
  }
}
