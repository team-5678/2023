// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.
//
package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import java.lang.Math;
import edu.wpi.first.math.MathUtil;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the
 * name of this class or
 * the package after creating this project, you must also update the
 * build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  Timer timer;
  Encoder m_encoder = new Encoder(3, 4);

  Spark m_left = new Spark(0);

  Spark m_right = new Spark(1);

  Spark m_elevator = new Spark(2);

  Spark m_left_claw = new Spark(3);

  Spark m_right_claw = new Spark(4);

  DifferentialDrive m_drive = new DifferentialDrive(m_left, m_right);

  DigitalInput toplimitSwitch = new DigitalInput(9);
  DigitalInput bottomlimitSwitch = new DigitalInput(0);

  Joystick joystick = new Joystick(0);

  double prevTwist = 0;
  double twist = 0;
  double timeElapsed = 0;
  double prevTimeElapsed = 0;

  boolean bottomHasBeenPressed = false;
  boolean topHasBeenPressed = false;

  /**
   * This function is run when the robot is first started up and should be used
   * for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    timer = new Timer();
    timer.start();
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    m_drive.setSafetyEnabled(false);
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items
   * like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different
   * autonomous modes using the dashboard. The sendable chooser code works with
   * the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the
   * chooser code and
   * uncomment the getString line to get the auto name from the text box below the
   * Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional comparisons to the
   * switch structure
   * below with additional strings. If using the SendableChooser make sure to add
   * them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    // switch (m_autoSelected) {
    // case kCustomAuto:
    // // Put custom auto code here
    // break;
    // case kDefaultAuto:
    // default:
    // // Put default auto code here
    // m_drive.arcadeDrive(.5, .5);
    // break;
    // }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {

    /**
     * Setting constants
     */
    double driveSpeed = .7;
    double elevatorSpeed = .5;
    double clawSpeed = .5;
    Boolean sprintButtonPressed = joystick.getRawButton(3);

    /**
     * Elevator control
     */
    switch (joystick.getPOV()) {
      // If POV pointed up
      case 180:
        if (!bottomlimitSwitch.get()) {
          // If bottom limit switch is activated, set speed zero
          System.out.println("LIMB");
          m_elevator.set(0);
        } else {
          // Otherwise, set speed to elevatorSpeed
          System.out.println("U");
          m_elevator.set(elevatorSpeed);
        }
        break;
      // If POV pointed down
      case 0:
        if (!toplimitSwitch.get()) {
          // If top limit switch is activated, set speed zero
          System.out.println("LIMT");
          m_elevator.set(0);
        } else {
          // Otherwise, set speed to elevatorSpeed
          System.out.println("D");
          m_elevator.set(-elevatorSpeed);
        }
        break;
      // Otherwise, set speed to zero
      default:
        m_elevator.set(0);
        break;
    }

    /**
     * Code for twist control; deprecated
     */
    // prevTwist = twist;
    // twist = joystick.getTwist();
    // double dtwist = MathUtil.clamp(((twist - prevTwist)) / .1, -1, 1);
    // prevTimeElapsed = timeElapsed;
    // timeElapsed = timer.get();
    // System.out.println(
    // String.format("Twist: %f, prevTwist: %f, timeElapsed: %f, prevTimeElapsed:
    // %f, dtwist: %f, %f, %f, %f", twist,
    // prevTwist,
    // timeElapsed, prevTimeElapsed, dtwist, (twist - prevTwist), (timeElapsed -
    // prevTimeElapsed),
    // (twist - prevTwist) / (timeElapsed - prevTimeElapsed)));

    /**
     * Drive control
     */
    m_drive.arcadeDrive(
        joystick.getX() * (sprintButtonPressed ? 1 : driveSpeed)/* * ((Math.cos(timeElapsed * 10) + 1) / 2) */,
        joystick.getY()
            * (sprintButtonPressed ? 1 : driveSpeed)/* * ((Math.cos(timeElapsed * 10) + 1) / 2) */ /* + dtwist */);

    /**
     * Intake control
     */
    if (joystick.getRawButton(1)) {
      // TODO: Determine if this is the correct sign
      // If trigger pressed, intake
      m_left_claw.set(clawSpeed);
      m_right_claw.set(-clawSpeed);
    } else if (joystick.getRawButton(2)) {
      // If button 2 pressed, outtake
      m_left_claw.set(-clawSpeed);
      m_right_claw.set(clawSpeed);
    } else {
      // Otherwise, set speed to zero
      m_left_claw.set(0);
      m_right_claw.set(0);
    }
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {
  }

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {
  }

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {
  }

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {
  }

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {
  }

  public void setMotorSpeed(double speed) {
    if (speed > 0) {
      if (toplimitSwitch.get()) {
        // We are going up and top limit is tripped so stop
        m_elevator.set(0);
      } else {
        // We are going up but top limit is not tripped so go at commanded speed
        m_elevator.set(speed);
      }
    } else {
      if (bottomlimitSwitch.get()) {
        // We are going down and bottom limit is tripped so stop
        m_elevator.set(0);
      } else {
        // We are going down but bottom limit is not tripped so go at commanded speed
        m_elevator.set(speed);
      }
    }
  }
}
