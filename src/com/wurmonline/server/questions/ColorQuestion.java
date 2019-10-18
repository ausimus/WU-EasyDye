package com.wurmonline.server.questions;

import com.wurmonline.server.Items;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.WurmColor;
import com.wurmonline.shared.constants.ItemMaterials;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ColorQuestion extends Question implements ItemMaterials, MiscConstants, QuestionTypes
{
    private Logger logger = Logger.getLogger(ColorQuestion.class.getName());

    public ColorQuestion(Creature aResponder, String aTitle, String aQuestion, long aTarget)
    {
        super(aResponder, aTitle, aQuestion, 9999, aTarget);
    }

    public void sendQuestion()
    {
        StringBuilder buf = new StringBuilder(getBmlHeader());
        short hw = 300;
        int c = 200;

        try
        {
            Item it = Items.getItem(target);
            String red = "";
            String green = "";
            String blue = "";
            if (it.getColor() != notInitialized)
            {
                red = Integer.toString(WurmColor.getColorRed(it.getColor()));
                green = Integer.toString(WurmColor.getColorGreen(it.getColor()));
                blue = Integer.toString(WurmColor.getColorBlue(it.getColor()));
            }
            buf.append("text{type='bold';text='Color 1'}");
            buf.append("harray{input{id='c_red'; maxchars='3'; text='").append(red).append("'}label{text='Red'}");
            buf.append("input{id='c_green'; maxchars='3'; text='").append(green).append("'}label{text='Green'}");
            buf.append("input{id='c_blue'; maxchars='3'; text='").append(blue).append("'}label{text='Blue'}}");
        }
        catch (NoSuchItemException ex)
        {
            logger.log(Level.WARNING, ex.getMessage(), ex);
        }

        buf.append(createAnswerButton2());
        getResponder().getCommunicator().sendBml(hw, hw, true, true, buf.toString(), c, c, c, title);
    }

    public void answer(Properties answers)
    {
        setAnswer(answers);
        parseColor(this);
    }

    private void parseColor(ColorQuestion question)
    {
        Creature responder = question.getResponder();
        long target = question.getTarget();
        try
        {
            Item item = Items.getItem(target);
            String red = question.getAnswer().getProperty("c_red");
            String green = question.getAnswer().getProperty("c_green");
            String blue = question.getAnswer().getProperty("c_blue");
            try
            {
                int r = Integer.parseInt(red);
                int g = Integer.parseInt(green);
                int b = Integer.parseInt(blue);
                if (r < 0 || r > 255)
                {
                    responder.getCommunicator().sendNormalServerMessage("Invalid color code for red " + r + ".");
                    return;
                }
                if (g < 0 || g > 255)
                {
                    responder.getCommunicator().sendNormalServerMessage("Invalid color code for green " + g + ".");
                    return;
                }
                if (b < 0 || b > 255)
                {
                    responder.getCommunicator().sendNormalServerMessage("Invalid color code for blue " + b + ".");
                    return;
                }
                item.setColor(WurmColor.createColor(r, g, b));
            }
            catch (NumberFormatException | NullPointerException ex)
            {
                item.setColor(notInitialized);
            }
            responder.getCommunicator().sendNormalServerMessage("Color changed.");
        }
        catch (NoSuchItemException ex)
        {
            logger.log(Level.WARNING, ex.getMessage(), ex);
        }
    }
}