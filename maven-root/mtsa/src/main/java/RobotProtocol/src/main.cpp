//============================================================================
// Name        : RobotProtocol.cpp
// Author      : 
// Version     :
// Copyright   : Your copyright notice
// Description : robot protocol test
//============================================================================
#define	SENDER				0
#define UPDATE_GAP			1

#define ROBOT_ID			4
#define MASTER_ID			1

#ifndef AVR
#include <iostream>
#endif

#include <stdio.h>
#include <WProgram.h>
#include "allMessages.h"
#include "allCommands.h"
#include "robotProtocol/SerialSenderReceiver.h"
#include "robotProtocol/MessageManager.h"
#include "robotProtocol/N6.h"


#ifndef AVR
using namespace std;
#endif
using namespace RobotProtocol;

int setupProgram(MessageManager *manager) {

	manager->addDeserializer(CommunicationStartMessage::MESSAGE_ID, CommunicationStartMessage::deserialize);
	manager->addDeserializer(CommunicationStartAcknowledgeMessage::MESSAGE_ID, CommunicationStartAcknowledgeMessage::deserialize);
	manager->addDeserializer(CommunicationStopMessage::MESSAGE_ID, CommunicationStopMessage::deserialize);

	manager->addDeserializer(CommandErrorMessage::MESSAGE_ID, CommandErrorMessage::deserialize);
	manager->addDeserializer(CommandAcceptedMessage::MESSAGE_ID, CommandAcceptedMessage::deserialize);

	manager->addDeserializer(SetMotorSpeedMessage::MESSAGE_ID, SetMotorSpeedMessage::deserialize);
	manager->addDeserializer(FollowLineMessage::MESSAGE_ID, FollowLineMessage::deserialize);
	manager->addDeserializer(TurnLeftMessage::MESSAGE_ID, TurnLeftMessage::deserialize);
	manager->addDeserializer(TurnRightMessage::MESSAGE_ID, TurnRightMessage::deserialize);
	manager->addDeserializer(TurnAroundMessage::MESSAGE_ID, TurnAroundMessage::deserialize);
	manager->addDeserializer(DestinationReachedMessage::MESSAGE_ID, DestinationReachedMessage::deserialize);
	manager->addDeserializer(RobotLostMessage::MESSAGE_ID, RobotLostMessage::deserialize);
	//manager->addDeserializer(ReadIRSensorsMessage::MESSAGE_ID, ReadIRSensorsMessage::deserialize);
	//manager->addDeserializer(ReadIRSensorsReplyMessage::MESSAGE_ID, ReadIRSensorsReplyMessage::deserialize);
	//manager->addDeserializer(ReadUSSensorMessage::MESSAGE_ID, ReadUSSensorMessage::deserialize);
	//manager->addDeserializer(RobotStatusMessage::MESSAGE_ID, RobotStatusMessage::deserialize);

	manager->addDeserializer(KeepAliveMessage::MESSAGE_ID, KeepAliveMessage::deserialize);

	return 0;
}

int main(void)
{
	SerialSenderReceiver	senderReceiver	= SerialSenderReceiver();
	MessageManager 			manager			= MessageManager(&senderReceiver, &senderReceiver);
	//if no master id is set the robot will respond to the first master to send a comm start message
	N6						robot			= N6(&manager, ROBOT_ID);
	//otherwise a fixed master could be set as:
	//N6(&manager, ROBOT_ID, MASTER_ID);

	setupProgram(&manager);

	robot.beepConfirm();
	//testCommunication(&manager);

	SetMotorSpeedCommand 	setSpeedCommand			= SetMotorSpeedCommand(&robot);
	FollowLineCommand		followLineCommand		= FollowLineCommand(&robot);


	TurnCommand				turnCommand				= TurnCommand(&robot);
	//ReadIRSensorsCommand	readIRSensorsCommand	= ReadIRSensorsCommand(&robot);
	//ReadUSSensorCommand		readUSSensorCommand		= ReadUSSensorCommand(&robot);

	robot.addCommand(SetMotorSpeedMessage::MESSAGE_ID, &setSpeedCommand);
	robot.addCommand(FollowLineMessage::MESSAGE_ID, &followLineCommand);
	robot.addCommand(TurnRightMessage::MESSAGE_ID, &turnCommand);
	robot.addCommand(TurnLeftMessage::MESSAGE_ID, &turnCommand);
	robot.addCommand(TurnAroundMessage::MESSAGE_ID, &turnCommand);
	//robot.addCommand(ReadIRSensorsMessage::MESSAGE_ID, &readIRSensorsCommand);
	//robot.addCommand(ReadUSSensorMessage::MESSAGE_ID, &readUSSensorCommand);

	while(true){
		robot.update(UPDATE_GAP);
		delay(UPDATE_GAP);
	}
	return 0;
}
