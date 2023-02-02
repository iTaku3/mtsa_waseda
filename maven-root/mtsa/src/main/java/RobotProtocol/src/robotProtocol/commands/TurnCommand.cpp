/*
 * TurnLeftCommand.cpp
 *
 *  Created on: Apr 17, 2013
 *      Author: mariano
 */

#include "TurnCommand.h"

namespace RobotProtocol {

TurnCommand::TurnCommand(N6* robot)
	:Command(robot){
	leftSensor	= 0;
	rightSensor	= 0;
	currentDelay= 0;
	step		= 0;
}

TurnCommand::~TurnCommand() {
}

bool TurnCommand::isContinuous(){
	return true;
}

void TurnCommand::reset(){
    robot->setLeftMotorSpeed(0);
    robot->setRightMotorSpeed(0);
    currentDelay		= 0;
    step				= 0;
    done				= false;
}


void TurnCommand::resetFromMessage(Message* message){
	switch(message->getMessageID()){
	case TurnLeftMessage::MESSAGE_ID:
		turnSpeed	= TURN_SPEED;
		turnDelay	= BLIND_DELAY;
		break;
	case TurnRightMessage::MESSAGE_ID:
		turnSpeed	= -TURN_SPEED;
		turnDelay	= BLIND_DELAY;
		break;
	case TurnAroundMessage::MESSAGE_ID:
		turnSpeed	= TURN_SPEED;
		turnDelay	= BLIND_DELAY * 2;
		break;
	}
    robot->setLeftMotorSpeed(FORTH_SPEED);
    robot->setRightMotorSpeed(FORTH_SPEED);
    currentDelay		= 0;
    step				= 0;
	done				= false;
}

void TurnCommand::execute(int updateGap){
    currentDelay	+= updateGap;

    if(step == 0){
    	if(currentDelay < FORTH_DELAY){
			return;
		}else{
		    robot->setLeftMotorSpeed(turnSpeed);
		    robot->setRightMotorSpeed(-turnSpeed);
			currentDelay	= 0;
			step++;
		}
    }else if(step == 1){
    	if(currentDelay < BLIND_DELAY){
			return;
		}else{
		    robot->setLeftMotorSpeed(turnSpeed);
		    robot->setRightMotorSpeed(-turnSpeed);
			currentDelay	= 0;
			step++;
		}
    }else if(step == 2){
    	if(currentDelay < BACK_DELAY){
			robot->setLeftMotorSpeed(-FORTH_SPEED);
			robot->setRightMotorSpeed(-FORTH_SPEED);
			return;
		}else{
	    	done			= true;
			step			= 0;
			currentDelay	= 0;

			robot->setLeftMotorSpeed(0);
			robot->setRightMotorSpeed(0);
			robot->getMessageManager()->sendMessage(
					*(ReusableMessageProxy::getDestinationReachedMessage(robot->getMasterId(), robot->getRobotId())));
			return;
		}
    }
    /*
    robot->getMessageManager()->sendMessage(
    			*(ReusableMessageProxy::getRobotStatusMessage(robot->getMasterId(), robot->getRobotId()
    						, robot->getLeftMotorSpeed(), robot->getRightMotorSpeed(), robot->getLeftIRSensor(), robot->getRightIRSensor()
    						, robot->getBatteryLeft())));
    						*/
	return;
}
} /* namespace RobotProtocol */
