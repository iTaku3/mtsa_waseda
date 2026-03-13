################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../src/Link\ to\ robotProtocol/Message.cpp \
../src/Link\ to\ robotProtocol/MessageManager.cpp \
../src/Link\ to\ robotProtocol/MessageManager_test.cpp \
../src/Link\ to\ robotProtocol/MessageReceiver.cpp \
../src/Link\ to\ robotProtocol/MessageReceiver_test.cpp \
../src/Link\ to\ robotProtocol/MessageSender.cpp \
../src/Link\ to\ robotProtocol/MessageSender_test.cpp \
../src/Link\ to\ robotProtocol/Message_test.cpp \
../src/Link\ to\ robotProtocol/SerialSenderReceiver.cpp \
../src/Link\ to\ robotProtocol/TestSenderReceiver.cpp \
../src/Link\ to\ robotProtocol/XBee.cpp \
../src/Link\ to\ robotProtocol/XBeeSenderReceiver.cpp 

OBJS += \
./src/Link\ to\ robotProtocol/Message.o \
./src/Link\ to\ robotProtocol/MessageManager.o \
./src/Link\ to\ robotProtocol/MessageManager_test.o \
./src/Link\ to\ robotProtocol/MessageReceiver.o \
./src/Link\ to\ robotProtocol/MessageReceiver_test.o \
./src/Link\ to\ robotProtocol/MessageSender.o \
./src/Link\ to\ robotProtocol/MessageSender_test.o \
./src/Link\ to\ robotProtocol/Message_test.o \
./src/Link\ to\ robotProtocol/SerialSenderReceiver.o \
./src/Link\ to\ robotProtocol/TestSenderReceiver.o \
./src/Link\ to\ robotProtocol/XBee.o \
./src/Link\ to\ robotProtocol/XBeeSenderReceiver.o 

CPP_DEPS += \
./src/Link\ to\ robotProtocol/Message.d \
./src/Link\ to\ robotProtocol/MessageManager.d \
./src/Link\ to\ robotProtocol/MessageManager_test.d \
./src/Link\ to\ robotProtocol/MessageReceiver.d \
./src/Link\ to\ robotProtocol/MessageReceiver_test.d \
./src/Link\ to\ robotProtocol/MessageSender.d \
./src/Link\ to\ robotProtocol/MessageSender_test.d \
./src/Link\ to\ robotProtocol/Message_test.d \
./src/Link\ to\ robotProtocol/SerialSenderReceiver.d \
./src/Link\ to\ robotProtocol/TestSenderReceiver.d \
./src/Link\ to\ robotProtocol/XBee.d \
./src/Link\ to\ robotProtocol/XBeeSenderReceiver.d 


# Each subdirectory must supply rules for building sources it contributes
src/Link\ to\ robotProtocol/Message.o: ../src/Link\ to\ robotProtocol/Message.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -funsigned-char -funsigned-bitfields -fno-exceptions -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to robotProtocol/Message.d" -MT"src/Link\ to\ robotProtocol/Message.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ robotProtocol/MessageManager.o: ../src/Link\ to\ robotProtocol/MessageManager.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -funsigned-char -funsigned-bitfields -fno-exceptions -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to robotProtocol/MessageManager.d" -MT"src/Link\ to\ robotProtocol/MessageManager.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ robotProtocol/MessageManager_test.o: ../src/Link\ to\ robotProtocol/MessageManager_test.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -funsigned-char -funsigned-bitfields -fno-exceptions -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to robotProtocol/MessageManager_test.d" -MT"src/Link\ to\ robotProtocol/MessageManager_test.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ robotProtocol/MessageReceiver.o: ../src/Link\ to\ robotProtocol/MessageReceiver.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -funsigned-char -funsigned-bitfields -fno-exceptions -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to robotProtocol/MessageReceiver.d" -MT"src/Link\ to\ robotProtocol/MessageReceiver.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ robotProtocol/MessageReceiver_test.o: ../src/Link\ to\ robotProtocol/MessageReceiver_test.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -funsigned-char -funsigned-bitfields -fno-exceptions -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to robotProtocol/MessageReceiver_test.d" -MT"src/Link\ to\ robotProtocol/MessageReceiver_test.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ robotProtocol/MessageSender.o: ../src/Link\ to\ robotProtocol/MessageSender.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -funsigned-char -funsigned-bitfields -fno-exceptions -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to robotProtocol/MessageSender.d" -MT"src/Link\ to\ robotProtocol/MessageSender.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ robotProtocol/MessageSender_test.o: ../src/Link\ to\ robotProtocol/MessageSender_test.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -funsigned-char -funsigned-bitfields -fno-exceptions -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to robotProtocol/MessageSender_test.d" -MT"src/Link\ to\ robotProtocol/MessageSender_test.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ robotProtocol/Message_test.o: ../src/Link\ to\ robotProtocol/Message_test.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -funsigned-char -funsigned-bitfields -fno-exceptions -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to robotProtocol/Message_test.d" -MT"src/Link\ to\ robotProtocol/Message_test.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ robotProtocol/SerialSenderReceiver.o: ../src/Link\ to\ robotProtocol/SerialSenderReceiver.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -funsigned-char -funsigned-bitfields -fno-exceptions -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to robotProtocol/SerialSenderReceiver.d" -MT"src/Link\ to\ robotProtocol/SerialSenderReceiver.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ robotProtocol/TestSenderReceiver.o: ../src/Link\ to\ robotProtocol/TestSenderReceiver.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -funsigned-char -funsigned-bitfields -fno-exceptions -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to robotProtocol/TestSenderReceiver.d" -MT"src/Link\ to\ robotProtocol/TestSenderReceiver.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ robotProtocol/XBee.o: ../src/Link\ to\ robotProtocol/XBee.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -funsigned-char -funsigned-bitfields -fno-exceptions -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to robotProtocol/XBee.d" -MT"src/Link\ to\ robotProtocol/XBee.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ robotProtocol/XBeeSenderReceiver.o: ../src/Link\ to\ robotProtocol/XBeeSenderReceiver.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -funsigned-char -funsigned-bitfields -fno-exceptions -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to robotProtocol/XBeeSenderReceiver.d" -MT"src/Link\ to\ robotProtocol/XBeeSenderReceiver.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


