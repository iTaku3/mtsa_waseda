################################################################################
# Automatically-generated file. Do not edit!
################################################################################

-include ../makefile.init

RM := rm -rf

# All of the sources participating in the build are defined here
-include sources.mk
-include src/robotProtocol/messages/subdir.mk
-include src/robotProtocol/commands/subdir.mk
-include src/robotProtocol/subdir.mk
-include src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/subdir.mk
-include src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/subdir.mk
-include src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/Class/Device/subdir.mk
-include src/DuinoBotKids.v1.xx_0022/DCMotor/subdir.mk
-include src/DuinoBotKids.v1.xx_0022/subdir.mk
-include src/subdir.mk
-include subdir.mk
-include objects.mk

ifneq ($(MAKECMDGOALS),clean)
ifneq ($(strip $(C++_DEPS)),)
-include $(C++_DEPS)
endif
ifneq ($(strip $(C_DEPS)),)
-include $(C_DEPS)
endif
ifneq ($(strip $(ASM_DEPS)),)
-include $(ASM_DEPS)
endif
ifneq ($(strip $(CC_DEPS)),)
-include $(CC_DEPS)
endif
ifneq ($(strip $(CPP_DEPS)),)
-include $(CPP_DEPS)
endif
ifneq ($(strip $(S_DEPS)),)
-include $(S_DEPS)
endif
ifneq ($(strip $(CXX_DEPS)),)
-include $(CXX_DEPS)
endif
ifneq ($(strip $(C_UPPER_DEPS)),)
-include $(C_UPPER_DEPS)
endif
ifneq ($(strip $(S_UPPER_DEPS)),)
-include $(S_UPPER_DEPS)
endif
endif

-include ../makefile.defs

# Add inputs and outputs from these tool invocations to the build variables 
LSS += \
RobotProtocol.lss \

FLASH_IMAGE += \
RobotProtocol.hex \

EEPROM_IMAGE += \
RobotProtocol.eep \

SIZEDUMMY += \
sizedummy \

AVRDUDEDUMMY += \


# All Target
all: RobotProtocol.elf secondary-outputs

# Tool invocations
RobotProtocol.elf: $(OBJS) $(USER_OBJS)
	@echo 'Building target: $@'
	@echo 'Invoking: AVR C++ Linker'
	avr-g++ -Wl,-Map,RobotProtocol.map,--cref -Wl,--gc-sections -mmcu=atmega32u4 -o "RobotProtocol.elf" $(OBJS) $(USER_OBJS) $(LIBS)
	@echo 'Finished building target: $@'
	@echo ' '

RobotProtocol.lss: RobotProtocol.elf
	@echo 'Invoking: AVR Create Extended Listing'
	-avr-objdump -h -S RobotProtocol.elf  >"RobotProtocol.lss"
	@echo 'Finished building: $@'
	@echo ' '

RobotProtocol.hex: RobotProtocol.elf
	@echo 'Create Flash image (ihex format)'
	-avr-objcopy -R .eeprom -O ihex RobotProtocol.elf  "RobotProtocol.hex"
	@echo 'Finished building: $@'
	@echo ' '

RobotProtocol.eep: RobotProtocol.elf
	@echo 'Create eeprom image (ihex format)'
	-avr-objcopy -j .eeprom --no-change-warnings --change-section-lma .eeprom=0 -O ihex RobotProtocol.elf  "RobotProtocol.eep"
	@echo 'Finished building: $@'
	@echo ' '

sizedummy: RobotProtocol.elf
	@echo 'Invoking: Print Size'
	-avr-size --format=avr --mcu=atmega32u4 RobotProtocol.elf
	@echo 'Finished building: $@'
	@echo ' '

avrdudedummy: RobotProtocol.elf
	@echo 'Invoking: AVRDude'
	/usr/bin/avrdude -pm32u4 -Uflash:w:RobotProtocol.hex:a
	@echo 'Finished building: $@'
	@echo ' '

# Other Targets
clean:
	-$(RM) $(OBJS)$(C_DEPS)$(EEPROM_IMAGE)$(ELFS)$(FLASH_IMAGE)$(LSS)$(S_DEPS)$(CXX_DEPS)$(S_UPPER_DEPS)$(C++_DEPS)$(ASM_DEPS)$(CC_DEPS)$(AVRDUDEDUMMY)$(CPP_DEPS)$(C_UPPER_DEPS)$(SIZEDUMMY) RobotProtocol.elf
	-@echo ' '

secondary-outputs: $(LSS) $(FLASH_IMAGE) $(EEPROM_IMAGE) $(SIZEDUMMY) $(AVRDUDEDUMMY)

.PHONY: all clean dependents
.SECONDARY:

-include ../makefile.targets
