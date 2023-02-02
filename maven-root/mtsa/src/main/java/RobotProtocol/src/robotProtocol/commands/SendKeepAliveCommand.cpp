/*
 * SendKeepAliveCommand.cpp
 *
 *  Created on: Apr 8, 2013
 *      Author: mariano
 */

#include "SendKeepAliveCommand.h"

namespace RobotProtocol {

SendKeepAliveCommand::SendKeepAliveCommand(N6 *robot, Message* message):Command(robot) {
	this->message	= message;
}

SendKeepAliveCommand::~SendKeepAliveCommand() {
	// TODO Auto-generated destructor stub
}

bool SendKeepAliveCommand::isContinuous(){
	return false;
}

void SendKeepAliveCommand::reset(){
	done				= false;
}


void SendKeepAliveCommand::resetFromMessage(Message* message){
}

void SendKeepAliveCommand::execute(int updateGap){
	robot->getMessageManager()->sendMessage(*message);
}

} /* namespace RobotProtocol */
