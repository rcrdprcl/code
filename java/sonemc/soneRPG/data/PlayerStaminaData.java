package sonemc.soneRPG.data;

public class PlayerStaminaData {
    private double stamina;
    private double maxStamina;
    private long lastStaminaUpdate;
    private boolean isRegenerating;
    private long regenStartTime;
    
    public PlayerStaminaData() {
        this.maxStamina = 100.0;
        this.stamina = 100.0;
        this.lastStaminaUpdate = System.currentTimeMillis();
        this.isRegenerating = false;
        this.regenStartTime = 0;
    }
    
    public void updateStamina() {
        long currentTime = System.currentTimeMillis();
        
        if (stamina <= 0 && !isRegenerating) {
            isRegenerating = true;
            regenStartTime = currentTime;
        }
        
        if (isRegenerating) {
            long timeSinceRegenStart = currentTime - regenStartTime;
            if (timeSinceRegenStart >= 15000) { // 15 seconds
                double regenAmount = (timeSinceRegenStart - 15000) / 1000.0 * (maxStamina / 10.0); // 10 seconds to full
                stamina = Math.min(maxStamina, regenAmount);
                
                if (stamina >= maxStamina) {
                    isRegenerating = false;
                }
            }
        } else if (stamina < maxStamina && stamina > 0) {
            long timeSinceUpdate = currentTime - lastStaminaUpdate;
            if (timeSinceUpdate >= 2000) { // 2 seconds delay before normal regen
                double regenAmount = (timeSinceUpdate - 2000) / 1000.0 * (maxStamina / 8.0); // 8 seconds to full
                stamina = Math.min(maxStamina, stamina + regenAmount);
            }
        }
        
        lastStaminaUpdate = currentTime;
    }
    
    public boolean consumeStamina(double amount) {
        if (stamina >= amount) {
            stamina -= amount;
            lastStaminaUpdate = System.currentTimeMillis();
            if (stamina <= 0) {
                stamina = 0;
                isRegenerating = true;
                regenStartTime = System.currentTimeMillis();
            }
            return true;
        }
        return false;
    }
    
    public boolean canSprint() {
        return stamina > 10.0;
    }
    
    public boolean canCriticalHit() {
        return stamina >= 25.0;
    }
    
    public String getStaminaBar() {
        double percentage = (stamina / maxStamina) * 100.0;
        int greenBars = (int) (percentage / 10.0);
        int redBars = 10 - greenBars;
        
        StringBuilder bar = new StringBuilder("§a");
        for (int i = 0; i < greenBars; i++) {
            bar.append("|");
        }
        bar.append("§c");
        for (int i = 0; i < redBars; i++) {
            bar.append("|");
        }
        bar.append(" §f(").append(String.format("%.0f", percentage)).append("%)");
        
        return bar.toString();
    }
    
    public double getStamina() { return stamina; }
    public void setStamina(double stamina) { this.stamina = Math.max(0, Math.min(maxStamina, stamina)); }
    public double getMaxStamina() { return maxStamina; }
    public void setMaxStamina(double maxStamina) { 
        this.maxStamina = maxStamina;
        if (stamina > maxStamina) stamina = maxStamina;
    }
    public double getStaminaPercentage() { return (stamina / maxStamina) * 100.0; }
    public boolean isRegenerating() { return isRegenerating; }
}