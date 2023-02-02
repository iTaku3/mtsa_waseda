/*
 * FollowLineCommand.cpp
 *
 *  Created on: Apr 16, 2013
 *      Author: mariano
 */

#include "FollowLineCommand.h"


namespace RobotProtocol {

FollowLineCommand::FollowLineCommand(N6* robot)
	:Command(robot){
	leftSensor	= 0;
	rightSensor	= 0;
	step		= 0;
	currentDelay= 0;
}

FollowLineCommand::~FollowLineCommand() {
}

bool FollowLineCommand::isContinuous(){
	return true;
}

void FollowLineCommand::reset(){
    robot->setLeftMotorSpeed(FOLLOW_SPEED);
    robot->setRightMotorSpeed(FOLLOW_SPEED);
	done				= false;
	step				= 0;
	currentDelay		= 0;
}


void FollowLineCommand::resetFromMessage(Message* message){
    robot->setLeftMotorSpeed(FOLLOW_SPEED);
    robot->setRightMotorSpeed(FOLLOW_SPEED);
	done				= false;
	step				= 0;
	currentDelay		= 0;
}

void FollowLineCommand::execute(int updateGap){

	//Read sensors:
    leftSensor		= robot->getLeftIRSensor();
    rightSensor		= robot->getRightIRSensor();

	currentDelay += updateGap;

	if(!robot->isOnWhite() && !robot->isLeftOnBlack()){
		int delta		= (leftSensor + rightSensor);
		delta			= ((FOLLOW_SPEED * (leftSensor - rightSensor))/delta);
		robot->setLeftMotorSpeed(FOLLOW_SPEED + delta);
		robot->setRightMotorSpeed(FOLLOW_SPEED - delta);
	}

    if(step == 0){
    	if(currentDelay > START_DELAY){
    		step++;
    		currentDelay	= 0;

    	}
    }else{
    	if(robot->isOnGray()){
		  robot->setLeftMotorSpeed(0);
		  robot->setRightMotorSpeed(0);
		  done			= true;
		  step			= 0;
		  currentDelay	= 0;
		  robot->getMessageManager()->sendMessage(
				  *(ReusableMessageProxy::getDestinationReachedMessage(robot->getMasterId(), robot->getRobotId())));
		  return;
		}else if(robot->isOnBlack())  //00
		{
		  robot->setLeftMotorSpeed(FOLLOW_SPEED);
		  robot->setRightMotorSpeed(FOLLOW_SPEED);
		}
		else if(robot->isOnWhite())
		{
		  robot->setLeftMotorSpeed(0);
		  robot->setRightMotorSpeed(0);
		  done			= true;
		  step			= 0;
		  currentDelay	= 0;

		  robot->getMessageManager()->sendMessage(
				  *(ReusableMessageProxy::getRobotLostMessage(robot->getMasterId(), robot->getRobotId())));
		  return;
		}
    }
    /*
	robot->getMessageManager()->sendMessage(
			*(ReusableMessageProxy::getRobotStatusMessage(robot->getMasterId(), robot->getRobotId()
						, robot->getLeftMotorSpeed(), robot->getRightMotorSpeed(), robot->getLeftIRSensor(), robot->getRightIRSensor()
						, robot->getBatteryLeft())));
						*/
}

} /* namespace RobotProtocol */
