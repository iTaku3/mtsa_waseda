################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/ConfigDescriptor.c \
../src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/DeviceStandardReq.c \
../src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/EndpointStream.c \
../src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/Events.c \
../src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/HostStandardReq.c \
../src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/PipeStream.c \
../src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/USBTask.c 

OBJS += \
./src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/ConfigDescriptor.o \
./src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/DeviceStandardReq.o \
./src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/EndpointStream.o \
./src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/Events.o \
./src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/HostStandardReq.o \
./src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/PipeStream.o \
./src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/USBTask.o 

C_DEPS += \
./src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/ConfigDescriptor.d \
./src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/DeviceStandardReq.d \
./src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/EndpointStream.d \
./src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/Events.d \
./src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/HostStandardReq.d \
./src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/PipeStream.d \
./src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/USBTask.d 


# Each subdirectory must supply rules for building sources it contributes
src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/%.o: ../src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/%.c
	@echo 'Building file: $<'
	@echo 'Invoking: AVR Compiler'
	avr-gcc -I"/home/mariano/Desktop/facultad/robotica/toolsRepo/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -std=gnu99 -funsigned-char -funsigned-bitfields -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


