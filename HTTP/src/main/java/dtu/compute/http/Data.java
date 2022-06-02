package dtu.compute.http;

import org.springframework.stereotype.Service;

@Service
public class Data implements IStatusComm{
    private String data;

    @Override
    public void updateGame(String gameState) {
        data = gameState;
    }

    @Override
    public String getGameState() {
        return data;
    }

    @Override
    public boolean conGreeting() {
        return true;
    }
}
