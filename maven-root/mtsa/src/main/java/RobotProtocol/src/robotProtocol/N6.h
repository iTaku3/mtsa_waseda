/*
 * N6.h
 *
 *  Created on: Apr 5, 2013
 *      Author: mariano
 */

#ifndef ROBOTPROTOCOL_N6_H_
#define ROBOTPROTOCOL_N6_H_


#include "MessageManager.h"
#include "Command.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <WProgram.h>
#include "messages/CommunicationStartMessage.h"
#include "messages/CommunicationStartAcknowledgeMessage.h"
#include "messages/CommunicationStopMessage.h"
#include "messages/SetMotorSpeedMessage.h"
#include "messages/KeepAliveMessage.h"
#include "messages/CommandErrorMessage.h"
#include "messages/CommandAcceptedMessage.h"


namespace RobotProtocol {

//forward declaration for circular reference
class Command;

class N6 {
public:
	static const int RANGE_PIN				= S1;
	static const long OBSTACLE_MAX_DIST	= 10;
	static const int BASE_FREQ       		= 440;
	static const int BUZZER_PIN        	= 23;
	static const int IR_RECEIVER_PIN   	= 14;
	static const int LEFT_LINE_SENSOR  	= 3; //Left from N6's point of view
	static const int RIGHT_LINE_SENSOR 	= 4; //Right from N6's point of view
	static const int TOP_IR_SENSOR			= S2;
	static const int BATTERY_PIN			= S2;
	static const int LE_WHITE_R     		= 880;
	static const int LE_WHITE_L     		= 880;
	static const int GE_BLACK_R     		= 150;
	static const int GE_BLACK_L     		= 150;
	static const int LE_GREY_R      		= 350;
	static const int LE_GREY_L      		= 350;
	static const int GE_GREY_R      		= 600;
	static const int GE_GREY_L      		= 600;
	static const int MASTER_UNDEFINED		= -1;

	N6(MessageManager *manager, int robotId);
	N6(MessageManager *manager, int robotId, int masterId);
	virtual ~N6();
	int getRobotId();
	int getMasterId();
	void update(int updateGap);
	bool addCommand(int mid, Command *command);
	bool setCommand(int mid);
	void setLeftMotorSpeed(float speed);
	void setRightMotorSpeed(float speed);
	float getLeftMotorSpeed();
	float getRightMotorSpeed();
	int getLeftIRSensor();
	int getRightIRSensor();
	int getBatteryLeft();
	int getTopIRSensor();
	int getUSSensorDistance();
	long getDistanceToObstacleInCentimeters();
	bool isOnWhite();
	bool isOnBlack();
	bool isAlmostOnBlack();
	bool isRightOnBlack();
	bool isLeftOnBlack();
	bool isOnGray();
	bool isOnTrack();
	bool isGoingLeft();
	bool isGoingRight();
	MessageManager* getMessageManager();
	void beepError();
	void beepConfirm();
	void beepMasterSet();
	void beepMasterClear();
private:
	void processCommandMessage(Message *message, int newCommandIndex);
	void initialize(MessageManager *manager, int robotId, int masterId);
	MessageManager *manager;
	static const int MAX_COMMAND_AMOUNT	= 10;
	Command* commands[MAX_COMMAND_AMOUNT];
	int commandsMids[MAX_COMMAND_AMOUNT];
	int lastCommandIndex;

	int robotId;
	int masterId;

	Command *currentCommand;
	int getMidIndex(int mid);
	void sendMessage(const char *message, int len);
	int cycles;
	float leftIRSensor, rightIRSensor, topIRSensor;
	KeepAliveMessage keepAliveMessage;
};
} /* namespace RobotProtocol */
#endif /* ROBOTPROTOCOL_N6_H_ */
