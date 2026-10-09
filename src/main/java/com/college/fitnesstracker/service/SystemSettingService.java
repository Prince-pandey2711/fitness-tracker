package com.college.fitnesstracker.service;

import com.college.fitnesstracker.model.SystemSetting;
import com.college.fitnesstracker.repository.SystemSettingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SystemSettingService {

    private final SystemSettingRepository systemSettingRepository;
    private final ActivityService activityService;

    public SystemSettingService(SystemSettingRepository systemSettingRepository, ActivityService activityService) {
        this.systemSettingRepository = systemSettingRepository;
        this.activityService = activityService;
    }

    public List<SystemSetting> getAllSettings() {
        return systemSettingRepository.findAllByOrderByCategoryAscSettingKeyAsc();
    }

    public String getSettingValue(String key, String defaultValue) {
        return systemSettingRepository.findBySettingKey(key)
                .map(SystemSetting::getSettingValue)
                .orElse(defaultValue);
    }

    public void updateSetting(String key, String value, String updatedByEmail) {
        Optional<SystemSetting> opt = systemSettingRepository.findBySettingKey(key);
        if (opt.isPresent()) {
            SystemSetting setting = opt.get();
            setting.setSettingValue(value);
            systemSettingRepository.save(setting);
            activityService.log(updatedByEmail, "SETTING_UPDATED", "Updated " + key + " = " + value, "ADMIN");
        }
    }

    public void updateMultipleSettings(Map<String, String> settingsMap, String updatedByEmail) {
        for (Map.Entry<String, String> entry : settingsMap.entrySet()) {
            Optional<SystemSetting> opt = systemSettingRepository.findBySettingKey(entry.getKey());
            if (opt.isPresent()) {
                SystemSetting setting = opt.get();
                setting.setSettingValue(entry.getValue());
                systemSettingRepository.save(setting);
            }
        }
        activityService.log(updatedByEmail, "SYSTEM_SETTINGS_UPDATED", "Updated system configurations", "ADMIN");
    }
}
