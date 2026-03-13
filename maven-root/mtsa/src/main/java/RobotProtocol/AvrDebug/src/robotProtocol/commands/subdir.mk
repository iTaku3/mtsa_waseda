################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../src/robotProtocol/commands/FollowLineCommand.cpp \
../src/robotProtocol/commands/ReadIRSensorsCommand.cpp \
../src/robotProtocol/commands/ReadUSSensorCommand.cpp \
../src/robotProtocol/commands/SendKeepAliveCommand.cpp \
../src/robotProtocol/commands/SetMotorSpeedCommand.cpp \
../src/robotProtocol/commands/TurnCommand.cpp 

OBJS += \
./src/robotProtocol/commands/FollowLineCommand.o \
./src/robotProtocol/commands/ReadIRSensorsCommand.o \
./src/robotProtocol/commands/ReadUSSensorCommand.o \
./src/robotProtocol/commands/SendKeepAliveCommand.o \
./src/robotProtocol/commands/SetMotorSpeedCommand.o \
./src/robotProtocol/commands/TurnCommand.o 

CPP_DEPS += \
./src/robotProtocol/commands/FollowLineCommand.d \
./src/robotProtocol/commands/ReadIRSensorsCommand.d \
./src/robotProtocol/commands/ReadUSSensorCommand.d \
./src/robotProtocol/commands/SendKeepAliveCommand.d \
./src/robotProtocol/commands/SetMotorSpeedCommand.d \
./src/robotProtocol/commands/TurnCommand.d 


# Each subdirectory must supply rules for building sources it contributes
src/robotProtocol/commands/%.o: ../src/robotProtocol/commands/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/home/marianoc/Desktop/repos/mtsa-code/trunk/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -funsigned-char -funsigned-bitfields -fno-exceptions -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


