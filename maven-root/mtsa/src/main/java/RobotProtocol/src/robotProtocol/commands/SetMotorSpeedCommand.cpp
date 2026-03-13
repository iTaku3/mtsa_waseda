/*
 * SetMotorSpeedCommand.cpp
 *
 *  Created on: Apr 8, 2013
 *      Author: mariano
 */

#include "SetMotorSpeedCommand.h"

namespace RobotProtocol {

SetMotorSpeedCommand::SetMotorSpeedCommand(N6* robot)
	:Command(robot){
}

SetMotorSpeedCommand::~SetMotorSpeedCommand() {
}

bool SetMotorSpeedCommand::isContinuous(){
	return false;
}

void SetMotorSpeedCommand::reset(){
	done				= true;
}


void SetMotorSpeedCommand::resetFromMessage(Message* message){
	SetMotorSpeedMessage* speedMessage	= (SetMotorSpeedMessage*)message;
	setLeftSpeed(speedMessage->getLeftSpeed());
	setRightSpeed(speedMessage->getRightSpeed());

	done				= true;
}

void SetMotorSpeedCommand::execute(int updateGap){
}


void SetMotorSpeedCommand::setLeftSpeed(int leftSpeed){
	this->leftSpeed		= leftSpeed;
	robot->setLeftMotorSpeed(leftSpeed);
}

void SetMotorSpeedCommand::setRightSpeed(int rightSpeed){
	this->rightSpeed	= rightSpeed;
	robot->setRightMotorSpeed(rightSpeed);
}

int SetMotorSpeedCommand::getLeftSpeed(){
	return leftSpeed;
}

int SetMotorSpeedCommand::getRightSpeed(){
	return rightSpeed;
}

} /* namespace RobotProtocol */
