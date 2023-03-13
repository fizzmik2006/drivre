// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import static edu.wpi.first.wpilibj.DoubleSolenoid.Value.*;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.cscore.VideoSource.ConnectionStrategy;
import edu.wpi.first.networktables.TimestampedString;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Joystick.ButtonType;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.AnalogAccelerometer;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "Pusher Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
private static final UsbCamera camera = new UsbCamera("DriverCamera", "dev/video0");

  
  private DifferentialDrive m_robot;
  private Joystick m_leftStick;
  private Joystick m_rightStick;
  // private static final int clawarmID = 7;
  // private static final int middlearmID = 6;
  private static final int backmotorID = 5;
  private static final int leftFrontDeviceID = 4;
  private static final int leftBackDeviceID = 3; 
  private static final int rightFrontDeviceID = 2;
  private static final int rightBackDeviceID = 1;
  private static final String VPH = null;
  private final Timer m_Timer = new Timer();
  CANSparkMax m_frontLeftMotor;
  CANSparkMax m_backLeftMotor;

  CANSparkMax m_frontRightMotor;
  CANSparkMax m_backRightMotor;

  CANSparkMax m_backmotor;
  CANSparkMax m_middlearm;
  CANSparkMax m_clawarm;
  DoubleSolenoid doubleSolenoid;

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("Pusher Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    camera.setConnectionStrategy(ConnectionStrategy.kAutoManage);
    
      
CameraServer.startAutomaticCapture(camera);
    CameraServer.putVideo("driverCamera", 480, 480);
    m_frontLeftMotor = new CANSparkMax(leftFrontDeviceID, MotorType.kBrushless);
    m_backLeftMotor = new CANSparkMax(leftBackDeviceID, MotorType.kBrushless);
    MotorControllerGroup m_left = new MotorControllerGroup(m_frontLeftMotor, m_backLeftMotor);

    m_frontRightMotor = new CANSparkMax(rightFrontDeviceID, MotorType.kBrushless);
    m_backRightMotor = new CANSparkMax(rightBackDeviceID, MotorType.kBrushless);
    MotorControllerGroup m_right = new MotorControllerGroup(m_frontRightMotor, m_backRightMotor);

    m_backmotor = new CANSparkMax(backmotorID, MotorType.kBrushless);
  

    m_robot = new DifferentialDrive(m_left, m_right);

    m_rightStick = new Joystick(0);  
    m_leftStick = new Joystick(1);    
    doubleSolenoid = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 0, 1);

  }



  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {}

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
    
    m_Timer.start();
    SmartDashboard.putNumber("timer", m_Timer.get());
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() { 
    //Autonomus Drive
   // Drive for 5 seconds
   if(m_chooser.getSelected() == kDefaultAuto) {
     if (m_Timer.get() < 2.0) {
      // Drive forwards half speed, make sure to turn input squaring off
      m_robot.arcadeDrive(0, -0.5, false);
    } else  if ((m_Timer.get()> 2) && (m_Timer.get()<3)) {
      m_robot.arcadeDrive(0, 0.5);
      
    } else {
      m_robot.stopMotor();
    }
  } else if (m_chooser.getSelected() == kCustomAuto ) {
    if(m_Timer.get()<2.5){
      doubleSolenoid.set(kReverse);
    } else if((m_Timer.get() > 2.5) && (m_Timer.get()<5.5)) {
      doubleSolenoid.set(kForward);
      m_robot.arcadeDrive(0, -0.5, false);
    } else if ((m_Timer.get() < 5.5) && (m_Timer.get() < 8.0)){
      m_robot.arcadeDrive(0, 0.5, false);
    } else {
     m_robot.stopMotor();
    }

  }
    }
  

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    SmartDashboard.putNumber("turn", m_rightStick.getY());
    m_robot.arcadeDrive(m_rightStick.getX(), -m_rightStick.getY());
    SmartDashboard.putNumber("forward", m_rightStick.getX());   

    

    /*
    if (m_leftStick.getRawButton(1)) {
      m_backmotor.set(m_leftStick.getThrottle());
    } else {
      m_backmotor.stopMotor();;
    }
    if (m_leftStick.getRawButton(2)) {
      
    }
    */

    if (m_leftStick.getRawButton(2)) {
      doubleSolenoid.set(Value.kForward);
    } else if (m_leftStick.getRawButton(1)) {
      doubleSolenoid.set(Value.kReverse);
    } else {
      doubleSolenoid.set(Value.kOff);
    }


  }
  
  /** This function is called o nce when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override










  
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {}

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {}
}
