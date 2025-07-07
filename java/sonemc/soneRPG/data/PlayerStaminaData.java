package sonemc.soneRPG.data;

public class PlayerStaminaData {
    private double stamina;
    private double maxStamina;
    private long lastStaminaUpdate;
    private long lastDamageTime;
    private long lastSprintTime;
    
    public PlayerStaminaData() {
        this.maxStamina = 100.0;
        this.stamina = 100.0;
        this.lastStaminaUpdate = System.currentTimeMillis();
        this.lastDamageTime = 0;
        this.lastSprintTime = 0;
    }
    
    public void updateStamina() {
        long currentTime = System.currentTimeMillis();
        long timeSinceUpdate = currentTime - lastStaminaUpdate;
        
        if (timeSinceUpdate < 1000) return;
        
        if (stamina < maxStamina) {
            long timeSinceLastDamage = currentTime - lastDamageTime;
            long timeSinceLastSprint = currentTime - lastSprintTime;
            
            if (timeSinceLastDamage >= 3000 && timeSinceLastSprint >= 2000) {
                if (stamina <= 0) {
                    if (timeSinceLastDamage >= 15000 && timeSinceLastSprint >= 15000) {
                        double regenRate = maxStamina / 10.0;
                        stamina = Math.min(maxStamina, stamina + regenRate);
                    }
                } else {
                    double regenRate = maxStamina / 8.0;
                    stamina = Math.min(maxStamina, stamina + regenRate);
                }
            }
        }
        
        lastStaminaUpdate = currentTime;
    }
    
    public boolean consumeStamina(double amount) {
        if (stamina >= amount) {
            stamina = Math.max(0, stamina - amount);
            lastStaminaUpdate = System.currentTimeMillis();
            return true;
        }
        return false;
    }
    
    public void onDamaged() {
        lastDamageTime = System.currentTimeMillis();
    }
    
    public void onSprint() {
        lastSprintTime = System.currentTimeMillis();
    }
    
    public boolean canSprint() {
        return stamina >= 10.0;
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
}